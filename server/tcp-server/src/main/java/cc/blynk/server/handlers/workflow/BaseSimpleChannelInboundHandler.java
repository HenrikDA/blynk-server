package cc.blynk.server.handlers.workflow;

import cc.blynk.common.exceptions.BaseServerException;
import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.server.auth.User;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.auth.session.SessionsHolder;
import cc.blynk.server.utils.FileManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public abstract class BaseSimpleChannelInboundHandler<I extends MessageBase> extends ChannelInboundHandlerAdapter {

    private static final Logger log = LogManager.getLogger(BaseSimpleChannelInboundHandler.class);
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
        if (acceptInboundMessage(msg)) {
            try {
                I imsg = (I) msg;
                User user = sessionsHolder.findUserByChannel(ctx.channel(), imsg.id);
                //setting logging context
                //todo how to handle exceptions??
                ThreadContext.put("user", user.getName());
                messageReceived(ctx, user, imsg);
            } finally {
                //cleanup logging context.
                ThreadContext.clearMap();
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
     * Returns {@code true} if the given message should be handled. If {@code false} it will be passed to the next
     * {@link io.netty.channel.ChannelInboundHandler} in the {@link io.netty.channel.ChannelPipeline}.
     */
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return matcher.match(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof BaseServerException) {
            BaseServerException baseServerException = (BaseServerException) cause;
            log.error(cause.getMessage(), cause);
            ctx.writeAndFlush(produce(baseServerException));
        } else {
            log.error("Unexpected error!!!", cause);
        }
    }
}
