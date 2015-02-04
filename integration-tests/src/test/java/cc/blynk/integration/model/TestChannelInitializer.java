package cc.blynk.integration.model;

import cc.blynk.common.handlers.decoders.ReplayingMessageDecoder;
import cc.blynk.common.handlers.encoders.DeviceMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
* The Blynk Project.
* Created by Dmitriy Dumanskiy.
* Created on 2/3/2015.
*/
public class TestChannelInitializer extends ChannelInitializer<SocketChannel> {

    private SimpleClientHandler responseMock;

    public TestChannelInitializer(SimpleClientHandler responseMock) {
        this.responseMock = responseMock;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //process input
        pipeline.addLast(new ReplayingMessageDecoder());
        //process output
        pipeline.addLast(new DeviceMessageEncoder());

        pipeline.addLast(responseMock);
    }
}
