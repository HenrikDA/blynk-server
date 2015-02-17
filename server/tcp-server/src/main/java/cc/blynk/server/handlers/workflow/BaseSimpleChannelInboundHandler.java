package cc.blynk.server.handlers.workflow;

import cc.blynk.common.exceptions.BaseServerException;
import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.DefaultExceptionHandler;
import cc.blynk.server.model.auth.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;
import org.apache.logging.log4j.ThreadContext;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public abstract class BaseSimpleChannelInboundHandler<I extends MessageBase> extends ChannelInboundHandlerAdapter implements DefaultExceptionHandler {

    protected final FileManager fileManager;
    protected final UserRegistry userRegistry;
    protected final SessionsHolder sessionsHolder;
    private final TypeParameterMatcher matcher;

    public BaseSimpleChannelInboundHandler(FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        this.fileManager = fileManager;
        this.userRegistry = userRegistry;
        this.sessionsHolder = sessionsHolder;
        this.matcher = TypeParameterMatcher.find(this, BaseSimpleChannelInboundHandler.class, "I");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (matcher.match(msg)) {
            User user = null;
            try {
                I imsg = (I) msg;
                user = sessionsHolder.findUserByChannel(ctx.channel(), imsg.id);
                user.incrStat(imsg.command);

                ThreadContext.put("user", user.getName());
                messageReceived(ctx, user, imsg);
                ThreadContext.clearMap();
            } catch (BaseServerException cause) {
                user.incrException(cause.errorCode);
                handleAppException(ctx, cause);
            } catch (Throwable t) {
                handleGeneralException(ctx, t);
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

}
