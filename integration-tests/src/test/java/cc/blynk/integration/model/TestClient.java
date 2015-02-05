package cc.blynk.integration.model;

import cc.blynk.client.Client;
import cc.blynk.integration.IntegrationBase;
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

    public SimpleClientHandler responseMock;
    private Channel clientChannel;
    private EventLoopGroup group;
    private int msgId = 0;

    public TestClient(String host, int port, TestChannelInitializer channelHandler) throws Exception {
        this.group = new NioEventLoopGroup();
        this.responseMock = channelHandler.responseMock;

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).handler(channelHandler);

        // Start the connection attempt.
        this.clientChannel = b.connect(host, port).sync().channel();
    }

    public TestClient send(String line) {
        clientChannel.writeAndFlush(Client.produceMessageBaseOnUserInput(line, ++msgId));

        //sleep after every send in order to retrieve response back from server.
        //it could be less, but for profiling 200 is more o less good
        IntegrationBase.sleep(200);

        return this;
    }

    public TestClient sendNoSleep(String line) {
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
