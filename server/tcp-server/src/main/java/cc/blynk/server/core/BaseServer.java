package cc.blynk.server.core;

import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.core.plain.Server;
import cc.blynk.server.handlers.workflow.BaseSimpleChannelInboundHandler;
import cc.blynk.server.model.auth.nio.ChannelServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/10/2015.
 */
public abstract class BaseServer implements Runnable {

    protected static final Logger log = LogManager.getLogger(Server.class);
    protected final int port;
    private final int workerThreads;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    protected BaseServer(int port, ServerProperties props) {
        this.port = port;
        this.workerThreads = props.getIntProperty("server.worker.threads", Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void run() {
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(workerThreads);
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup)
                    .channel(ChannelServer.class)
                    .childHandler(getChannelInitializer());

            ChannelFuture channelFuture = b.bind(port).sync();
            Channel channel = channelFuture.channel();

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            stop();
        }
    }

    public abstract List<BaseSimpleChannelInboundHandler> getBaseHandlers();

    public abstract ChannelInitializer<SocketChannel> getChannelInitializer();

    public void stop() {
        try {
            workerGroup.shutdownGracefully().await();
            bossGroup.shutdownGracefully().await();
        } catch (InterruptedException e) {
            log.error("Error waiting server shutdown.");
        }
    }
}
