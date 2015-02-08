package cc.blynk.server.handlers;

import cc.blynk.common.enums.Command;
import cc.blynk.common.exceptions.BaseServerException;
import cc.blynk.common.model.messages.ResponseMessage;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.auth.session.SessionsHolder;
import cc.blynk.server.utils.FileManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public abstract class BaseSimpleChannelInboundHandler<I> extends SimpleChannelInboundHandler<I> {

    private static final Logger log = LogManager.getLogger(BaseSimpleChannelInboundHandler.class);

    protected FileManager fileManager;
    protected UserRegistry userRegistry;
    protected SessionsHolder sessionsHolder;

    public BaseSimpleChannelInboundHandler(FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        this.fileManager = fileManager;
        this.userRegistry = userRegistry;
        this.sessionsHolder = sessionsHolder;
    }

    private static ResponseMessage produce(BaseServerException exception) {
        return new ResponseMessage(exception.msgId, Command.RESPONSE, exception.errorCode);
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
