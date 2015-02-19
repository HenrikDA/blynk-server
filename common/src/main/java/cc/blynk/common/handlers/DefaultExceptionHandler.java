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
            handleUnexpectedException(ctx, cause);
        }
    }

    public default void handleUnexpectedException(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //all this are expected when user goes offline without closing socket correctly...
        if ("Connection reset by peer".equals(cause.getMessage()) ||
                "No route to host".equals(cause.getMessage()) ||
                "Connection timed out".equals(cause.getMessage())) {
            log.error("Client goes offline. Reason : {}", cause.getMessage());
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
