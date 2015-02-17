package cc.blynk.common.handlers;

import cc.blynk.common.exceptions.BaseServerException;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/11/2015.
 */
public interface DefaultExceptionHandler {

    static final Logger log = LogManager.getLogger(DefaultExceptionHandler.class);

    public default void handleGeneralException(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof BaseServerException) {
            handleAppException(ctx, (BaseServerException) cause);
        } else {
            log.error("Unexpected error!!!", cause);
        }
    }

    public default void handleAppException(ChannelHandlerContext ctx, BaseServerException baseServerException) {
        //no need for stack trace for known exceptions
        log.error(baseServerException.getMessage());
        try {
            //todo handle exception here?
            ctx.writeAndFlush(produce(baseServerException));
        } finally {
            //cleanup logging context in case error happened.
            ThreadContext.clearMap();
        }
    }

}
