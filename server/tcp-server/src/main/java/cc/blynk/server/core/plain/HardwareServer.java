package cc.blynk.server.core.plain;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.core.BaseHandlersHolder;
import cc.blynk.server.core.BaseServer;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.workflow.BaseSimpleChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class HardwareServer extends BaseServer {

    private final BaseHandlersHolder handlersHolder;
    private final ChannelInitializer<SocketChannel> channelInitializer;

    public HardwareServer(ServerProperties props, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder, GlobalStats stats) {
        super(props.getIntProperty("server.default.port"), props);

        this.handlersHolder = new HardwareHandlersHolder(props, fileManager, userRegistry, sessionsHolder);
        this.channelInitializer = new HardwareChannelInitializer(sessionsHolder, stats, handlersHolder);

        log.info("Hardware server port {}.", port);
    }

    @Override
    public List<BaseSimpleChannelInboundHandler> getBaseHandlers() {
        return handlersHolder.getBaseHandlers();
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }

    @Override
    public void stop() {
        log.info("Shutting down default server...");
        super.stop();
    }

}
