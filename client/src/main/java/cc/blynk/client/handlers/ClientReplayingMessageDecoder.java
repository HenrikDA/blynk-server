package cc.blynk.client.handlers;

import cc.blynk.common.handlers.decoders.ReplayingMessageDecoder;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/20/2015.
 */
public class ClientReplayingMessageDecoder extends ReplayingMessageDecoder {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //server goes down
        if (cause instanceof IOException && "An existing connection was forcibly closed by the remote host".equals(cause.getMessage())) {
            ctx.close();
            log.error("Server went down. Shutting down...");
            //todo find better way
            System.exit(1);
        }
        super.exceptionCaught(ctx, cause);
    }
}
