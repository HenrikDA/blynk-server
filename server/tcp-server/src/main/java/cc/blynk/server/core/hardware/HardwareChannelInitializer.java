package cc.blynk.server.core.hardware;

import cc.blynk.common.handlers.decoders.ReplayingMessageDecoder;
import cc.blynk.common.handlers.encoders.DeviceMessageEncoder;
import cc.blynk.common.stats.GlobalStats;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.handlers.workflow.ClientChannelStateHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
* The Blynk Project.
* Created by Dmitriy Dumanskiy.
* Created on 11.03.15.
*/
final class HardwareChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SessionsHolder sessionsHolder;
    private final GlobalStats stats;
    private final HardwareHandlersHolder handlersHolder;

    public HardwareChannelInitializer(SessionsHolder sessionsHolder, GlobalStats stats, HardwareHandlersHolder handlersHolder) {
        this.sessionsHolder = sessionsHolder;
        this.stats = stats;
        this.handlersHolder = handlersHolder;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //non-sharable handlers
        pipeline.addLast(new ClientChannelStateHandler(sessionsHolder));
        pipeline.addLast(new ReplayingMessageDecoder(stats));
        pipeline.addLast(new DeviceMessageEncoder());

        //sharable business logic handlers initialized previously
        handlersHolder.getAllHandlers().forEach(pipeline::addLast);
    }
}
