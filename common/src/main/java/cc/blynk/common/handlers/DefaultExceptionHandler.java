package cc.blynk.common.handlers;

import cc.blynk.common.exceptions.BaseServerException;
import cc.blynk.common.exceptions.UnsupportedCommandException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.ssl.NotSslRecordException;
import io.netty.handler.timeout.ReadTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.net.ssl.SSLException;

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
        if (cause instanceof ReadTimeoutException) {
            log.trace("Channel was inactive for a long period. Closing...");
            //channel is already closed here by ReadTimeoutHandler
        } else if (cause instanceof DecoderException && cause.getCause() instanceof UnsupportedCommandException) {
            log.error("Input command is invalid. Closing socket.", cause.getMessage());
            ctx.close();
        } else if (cause instanceof NotSslRecordException) {
            log.error("Not secure connection attempt detected. {}.", cause.getMessage());
            ctx.close();
        } else if (cause instanceof SSLException) {
            log.error("SSL exception. {}.", cause.getMessage());
            ctx.close();
        } else {
            String errorMessage = cause.getMessage() == null ? "" : cause.getMessage();

            //all this are expected when user goes offline without closing socket correctly...
            switch (errorMessage) {
                case "Connection reset by peer":
                case "No route to host":
                case "Connection timed out":
                    log.debug("Client goes offline. Reason : {}", cause.getMessage());
                    break;
                default:
                    log.error("Unexpected error!!!", cause);
                    break;
            }
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
