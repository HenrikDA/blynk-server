package cc.blynk.integration.model;

import cc.blynk.client.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/31/2015.
 */
public class TestClient {

    private TestChannelInitializer channelHandler;
    private Channel clientChannel;
    private EventLoopGroup group;
    private int msgId = 0;

    public TestClient(String host, int port, TestChannelInitializer channelHandler) throws Exception {
        this.group = new NioEventLoopGroup();
        this.channelHandler = channelHandler;

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).handler(channelHandler);

        // Start the connection attempt.
        this.clientChannel = b.connect(host, port).sync().channel();
    }

    public TestClient send(String line) {
        if (clientChannel.isWritable()) {
            clientChannel.writeAndFlush(Client.produceMessageBaseOnUserInput(line, ++msgId));
        }
        return this;
    }


    public void reset() {
        msgId = 0;
    }

    public SimpleClientHandler getSimpleClientHandler() {
        return channelHandler.responseMock;
    }

    public void replace(SimpleClientHandler simpleClientHandler) {
        channelHandler.pipeline.removeLast();
        channelHandler.pipeline.addLast(simpleClientHandler);
    }

    public void close() {
        clientChannel.close();
        group.shutdownGracefully();
    }

}
