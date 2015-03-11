package cc.blynk.server.handlers.workflow;

import cc.blynk.common.exceptions.BaseServerException;
import cc.blynk.common.handlers.DefaultExceptionHandler;
import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.exceptions.UserNotAuthenticated;
import cc.blynk.server.model.auth.User;
import cc.blynk.server.model.auth.nio.ChannelState;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;
import org.apache.logging.log4j.ThreadContext;

import java.util.Properties;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public abstract class BaseSimpleChannelInboundHandler<I extends MessageBase> extends ChannelInboundHandlerAdapter implements DefaultExceptionHandler {

    protected final Properties props;
    protected final FileManager fileManager;
    protected final UserRegistry userRegistry;
    protected final SessionsHolder sessionsHolder;
    private final TypeParameterMatcher matcher;
    private volatile int USER_QUOTA_LIMIT;

    public BaseSimpleChannelInboundHandler(ServerProperties props, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        this.props = props;
        this.fileManager = fileManager;
        this.userRegistry = userRegistry;
        this.sessionsHolder = sessionsHolder;
        this.matcher = TypeParameterMatcher.find(this, BaseSimpleChannelInboundHandler.class, "I");
        updateProperties(props);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (matcher.match(msg)) {
            User user = null;
            try {
                I imsg = (I) msg;
                user = ((ChannelState) ctx.channel()).user;
                if (user == null) {
                    throw new UserNotAuthenticated("User not logged.", imsg.id);
                }
                if (user.getQuotaMeter().getOneMinuteRate() > USER_QUOTA_LIMIT) {
                    //throw new UserQuotaLimitExceededException("Quota limit exceeded.", imsg.id);
                    //this is special case. discard request in case of high request rate.
                    //todo avoid printing this every time???
                    log.warn("User '{}' had exceeded {} rec/sec limit.", user.getName(), USER_QUOTA_LIMIT);
                    return;
                }
                user.incrStat(imsg.command);

                ThreadContext.put("user", user.getName());
                messageReceived(ctx, user, imsg);
                ThreadContext.clearMap();
            } catch (BaseServerException cause) {
                if (user != null) {
                    user.incrException(cause.errorCode);
                }
                handleAppException(ctx, cause);
            } catch (Exception e) {
                handleUnexpectedException(e);
            } finally {
                ReferenceCountUtil.release(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * <strong>Please keep in mind that this method will be renamed to
     * {@code messageReceived(ChannelHandlerContext, I)} in 5.0.</strong>
     *
     * Is called for each message of type {@link I}.
     *
     * @param ctx           the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *                      belongs to
     * @param msg           the message to handle
     * @throws Exception    is thrown if an error occurred
     */
    protected abstract void messageReceived(ChannelHandlerContext ctx, User user, I msg) throws Exception;

    /**
     *  When property file changed during server work, to avoid restart,
     *  so every child overrides it's property.
     */
    public void updateProperties(ServerProperties props) {
        this.USER_QUOTA_LIMIT = props.getIntProperty("user.message.quota.limit");
    }
}
