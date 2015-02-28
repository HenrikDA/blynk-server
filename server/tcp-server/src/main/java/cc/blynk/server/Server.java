package cc.blynk.server;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.common.utils.PropertiesUtil;
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

import java.util.Properties;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class Server implements Runnable {

    private static final Logger log = LogManager.getLogger(Server.class);

    protected int port;

    protected Properties props;
    protected UserRegistry userRegistry;
    protected FileManager fileManager;
    protected SessionsHolder sessionsHolder;
    protected GlobalStats stats;

    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workerGroup;

    protected Server() {

    }

    public Server(Properties props, FileManager fileManager, SessionsHolder sessionsHolder, UserRegistry userRegistry, GlobalStats stats) {
        this.props = props;
        this.fileManager = fileManager;
        this.sessionsHolder = sessionsHolder;
        this.userRegistry = userRegistry;
        this.stats = stats;
        this.port = PropertiesUtil.getIntProperty(props, "server.default.port");
        log.info("Server listening on : {} port.", port);
    }

    @Override
    public void run() {
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup)
                    .channel(ChannelServer.class)
                    .childHandler(getServerHandlersInitializer());

            ChannelFuture channelFuture = b.bind(port).sync();
            Channel channel = channelFuture.channel();

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            stop();
        }
    }

    protected ServerHandlersInitializer getServerHandlersInitializer() {
        return new ServerHandlersInitializer(props, fileManager, userRegistry, sessionsHolder, stats);
    }

    public void stop() {
        log.info("Shutting down server...");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
