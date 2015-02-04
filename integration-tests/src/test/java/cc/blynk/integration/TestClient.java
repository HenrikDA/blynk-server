package cc.blynk.integration;

import cc.blynk.client.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/31/2015.
 */
public class TestClient {

    private Channel clientChannel;
    private EventLoopGroup group;
    private int msgId = 0;

    public TestClient(String host, int port, ChannelHandler channelHandler) throws Exception {
        this.group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).handler(channelHandler);

        // Start the connection attempt.
        this.clientChannel = b.connect(host, port).sync().channel();
    }

    public TestClient sendWithSleep(String line) {
        clientChannel.writeAndFlush(Client.produceMessageBaseOnUserInput(line, ++msgId));

        //sleep after every send in order to retrieve response back from server.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        return this;
    }

    public TestClient send(String line) {
        clientChannel.writeAndFlush(Client.produceMessageBaseOnUserInput(line, ++msgId));
        return this;
    }

    public void reset() {
        msgId = 0;
    }

    public void close() {
        clientChannel.close();
        group.shutdownGracefully();
    }

}
