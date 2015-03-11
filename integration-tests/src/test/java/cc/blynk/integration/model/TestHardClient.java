package cc.blynk.integration.model;

import cc.blynk.client.core.HardwareClient;
import cc.blynk.common.handlers.decoders.ReplayingMessageDecoder;
import cc.blynk.common.handlers.encoders.DeviceMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.mockito.Mockito;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/31/2015.
 */
public class TestHardClient extends HardwareClient {

    public final SimpleClientHandler responseMock;
    private ChannelPipeline pipeline;

    private int msgId;

    public TestHardClient(String host, int port) {
        super(host, port);

        this.responseMock = Mockito.mock(SimpleClientHandler.class);
        this.msgId = 0;
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                TestHardClient.this.pipeline = pipeline;

                pipeline.addLast(new ReplayingMessageDecoder());
                pipeline.addLast(new DeviceMessageEncoder());
                pipeline.addLast(responseMock);
            }
        };
    }

    public TestHardClient send(String line) {
        send(produceMessageBaseOnUserInput(line, ++msgId));
        return this;
    }

    public void reset() {
        msgId = 0;
    }

    public void replace(SimpleClientHandler simpleClientHandler) {
        pipeline.removeLast();
        pipeline.addLast(simpleClientHandler);
    }

}
