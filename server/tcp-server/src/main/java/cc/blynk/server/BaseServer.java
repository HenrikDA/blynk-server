package cc.blynk.server;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.workflow.BaseSimpleChannelInboundHandler;
import cc.blynk.server.model.auth.ChannelServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/16/2015.
 */
public abstract class BaseServer implements Runnable {

    private static final Logger log = LogManager.getLogger(BaseServer.class);

    protected int port;

    protected Properties props;
    protected UserRegistry userRegistry;
    protected FileManager fileManager;
    protected SessionsHolder sessionsHolder;
    protected GlobalStats stats;

    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workerGroup;
    protected BaseSimpleChannelInboundHandler[] handlers;

    protected BaseServer(Properties props, FileManager fileManager, SessionsHolder sessionsHolder, UserRegistry userRegistry, GlobalStats stats) {
        this.props = props;
        this.fileManager = fileManager;
        this.sessionsHolder = sessionsHolder;
        this.userRegistry = userRegistry;
        this.stats = stats;
    }

    @Override
    public void run() {
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup)
                    .channel(ChannelServer.class)
                    //.handler(new LoggingHandler())
                    .childHandler(getServerHandlersInitializer());

            ChannelFuture channelFuture = b.bind(port).sync();

            Channel channel = channelFuture.channel();
            this.handlers = fetchHandlers(channel.pipeline());

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            stop();
        }
    }

    private BaseSimpleChannelInboundHandler[] fetchHandlers(ChannelPipeline pipeline) {
        List<BaseSimpleChannelInboundHandler> handlerList = new ArrayList<>();
        for (Map.Entry<String, ChannelHandler> entry : pipeline) {
            if (entry.getValue() instanceof BaseSimpleChannelInboundHandler) {
                handlerList.add((BaseSimpleChannelInboundHandler) entry.getValue());
            }
        }

        return handlerList.toArray(new BaseSimpleChannelInboundHandler[handlerList.size()]);
    }

    public BaseSimpleChannelInboundHandler[] getHandlers() {
        return handlers;
    }

    protected ServerHandlersInitializer getServerHandlersInitializer() {
        return new ServerHandlersInitializer(props, fileManager, userRegistry, sessionsHolder, stats);
    }

    protected void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
