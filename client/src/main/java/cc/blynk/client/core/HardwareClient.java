package cc.blynk.client.core;

import cc.blynk.client.handlers.ClientReplayingMessageDecoder;
import cc.blynk.common.handlers.encoders.DeviceMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Random;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.03.15.
 */
public class HardwareClient extends BaseClient {

    public HardwareClient(String host, int port) {
        super(host, port, new Random());
        log.info("Creating hardware client. Host : {}, port : {}", host, port);
    }

    public HardwareClient(String host, int port, Random msgIdGenerator) {
        super(host, port, msgIdGenerator);
        log.info("Creating hardware client. Host : {}, port : {}", host, port);
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel> () {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ClientReplayingMessageDecoder());
                pipeline.addLast(new DeviceMessageEncoder());
            }
        };
    }
}
