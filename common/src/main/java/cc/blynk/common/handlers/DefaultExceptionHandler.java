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
            handleUnexpectedException(cause);
        }
    }

    public default void handleUnexpectedException(Throwable cause) throws Exception {
        //all this are expected when user goes offline without closing socket correctly...
        if (cause.getCause() == null) {
            log.error("Unexpected error!!!", cause);
            return;
        }
        switch (cause.getMessage()) {
            case "Connection reset by peer" :
            case "No route to host" :
            case "Connection timed out" :
                log.error("Client goes offline. Reason : {}", cause.getMessage());
                break;
            default :
                log.error("Unexpected error!!!", cause);
                break;
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
