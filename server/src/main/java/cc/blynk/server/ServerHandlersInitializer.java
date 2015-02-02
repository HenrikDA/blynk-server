package cc.blynk.server;

import cc.blynk.common.handlers.decoders.ReplayingMessageDecoder;
import cc.blynk.common.handlers.encoders.DeviceMessageEncoder;
import cc.blynk.server.handlers.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class ServerHandlersInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //process input
        pipeline.addLast(new ReplayingMessageDecoder());

        //process output
        pipeline.addLast(new DeviceMessageEncoder());

        //business logic
        pipeline.addLast(new RegisterHandler());
        pipeline.addLast(new LoginHandler());
        pipeline.addLast(new GetTokenHandler());
        pipeline.addLast(new LoadProfileHandler());
        pipeline.addLast(new SaveProfileHandler());
        pipeline.addLast(new HardwareHandler());
    }
}

