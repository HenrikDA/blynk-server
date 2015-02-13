package cc.blynk.server;

import cc.blynk.common.handlers.decoders.ReplayingMessageDecoder;
import cc.blynk.common.handlers.encoders.DeviceMessageEncoder;
import cc.blynk.common.stats.GlobalStats;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.auth.LoginHandler;
import cc.blynk.server.handlers.auth.RegisterHandler;
import cc.blynk.server.handlers.workflow.*;
import cc.blynk.server.twitter.TwitterWrapper;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class ServerHandlersInitializer extends ChannelInitializer<SocketChannel> {

    private FileManager fileManager;
    private UserRegistry userRegistry;
    private SessionsHolder sessionsHolder;
    private GlobalStats stats;

    public ServerHandlersInitializer(FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder, GlobalStats stats) {
        this.fileManager = fileManager;
        this.userRegistry = userRegistry;
        this.sessionsHolder = sessionsHolder;
        this.stats = stats;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //process input
        pipeline.addLast(new ReplayingMessageDecoder(stats));

        //process output
        pipeline.addLast(new DeviceMessageEncoder());

        //business logic
        pipeline.addLast(new RegisterHandler(fileManager, userRegistry, sessionsHolder));
        pipeline.addLast(new LoginHandler(fileManager, userRegistry, sessionsHolder));
        pipeline.addLast(new GetTokenHandler(fileManager, userRegistry, sessionsHolder));
        pipeline.addLast(new LoadProfileHandler(fileManager, userRegistry, sessionsHolder));
        pipeline.addLast(new SaveProfileHandler(fileManager, userRegistry, sessionsHolder));
        pipeline.addLast(new HardwareHandler(fileManager, userRegistry, sessionsHolder));
        pipeline.addLast(new PingHandler(fileManager, userRegistry, sessionsHolder));
        pipeline.addLast(new TweetHandler(fileManager, userRegistry, sessionsHolder, new TwitterWrapper()));
    }
}

