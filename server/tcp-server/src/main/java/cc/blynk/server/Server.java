package cc.blynk.server;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.model.auth.ChannelServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class Server implements Runnable {

    private static final Logger log = LogManager.getLogger(Server.class);

    protected int port;
    protected HandlersHolder handlersHolder;
    protected ServerHandlersInitializer serverHandlersInitializer;

    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workerGroup;

    protected Server() {
    }

    public Server(ServerProperties props, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder, GlobalStats stats) {
        this.port = props.getIntProperty("server.default.port");
        this.handlersHolder = new HandlersHolder(props, fileManager, userRegistry, sessionsHolder);
        this.serverHandlersInitializer = new ServerHandlersInitializer(handlersHolder, stats);
        log.info("Default server port {}.", port);
    }

    @Override
    public void run() {
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup)
                    .channel(ChannelServer.class)
                    .childHandler(serverHandlersInitializer);

            ChannelFuture channelFuture = b.bind(port).sync();
            Channel channel = channelFuture.channel();

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            stop();
        }
    }

    public HandlersHolder getHandlersHolder() {
        return handlersHolder;
    }

    public void stop() {
        log.info("Shutting down default server...");
        try {
            workerGroup.shutdownGracefully().await();
            bossGroup.shutdownGracefully().await();
        } catch (InterruptedException e) {
            log.error("Error waiting server shutdown.");
        }
    }

}
