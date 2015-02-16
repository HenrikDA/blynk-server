package cc.blynk.server.handlers.logging;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketAddress;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class LoggingHandler extends ChannelDuplexHandler {

    private static final Logger log = LogManager.getLogger(LoggingHandler.class);

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise future) throws Exception {
        super.bind(ctx, localAddress, future);
        log.info("Server started.");
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
        log.info("Closing.!!!1");
        super.close(ctx, future);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
        log.info("Diconecting!!!!");
        super.disconnect(ctx, future);
    }
}
