package cc.blynk.server.handlers;

import cc.blynk.common.exceptions.BaseServerException;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/11/2015.
 */
public interface DefaultExceptionHandler {

    static final Logger log = LogManager.getLogger(DefaultExceptionHandler.class);

    //do not delete! it is used in all childs!
    public default void handleException(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof BaseServerException) {
            BaseServerException baseServerException = (BaseServerException) cause;
            //no need for stack trace for known exceptions
            log.error(cause.getMessage());
            ctx.writeAndFlush(produce(baseServerException));
        } else {
            log.error("Unexpected error!!!", cause);
        }
    }

}
