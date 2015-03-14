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
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        throw new IOException("Server closed client connection.");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //server goes down
        if (cause instanceof IOException) {
            ctx.close();
            log.error("Client socket closed. Reason : {}", cause.getMessage());
            //todo find better way
            System.exit(0);
        }
    }
}
