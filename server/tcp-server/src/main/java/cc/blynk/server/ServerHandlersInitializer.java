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
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import java.util.Properties;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class ServerHandlersInitializer extends ChannelInitializer<SocketChannel> {

    private Properties props;
    private SslContext sslCtx;
    private FileManager fileManager;
    private UserRegistry userRegistry;
    private SessionsHolder sessionsHolder;
    private GlobalStats stats;

    //sharable handlers
    private RegisterHandler registerHandler;
    private LoginHandler loginHandler;
    private GetTokenHandler getTokenHandler;
    private LoadProfileHandler loadProfileHandler;
    private SaveProfileHandler saveProfileHandler;
    private HardwareHandler hardwareHandler;
    private PingHandler pingHandler;
    private TweetHandler tweetHandler;

    public ServerHandlersInitializer(Properties props, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder, GlobalStats stats, SslContext sslCtx) {
        this.props = props;
        this.fileManager = fileManager;
        this.userRegistry = userRegistry;
        this.sessionsHolder = sessionsHolder;
        this.stats = stats;
        this.sslCtx = sslCtx;
        initHandlers();
    }

    public ServerHandlersInitializer(Properties props, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder, GlobalStats stats) {
        this(props, fileManager, userRegistry, sessionsHolder, stats, null);
    }

    private void initHandlers() {
        registerHandler = new RegisterHandler(fileManager, userRegistry, sessionsHolder);
        loginHandler = new LoginHandler(fileManager, userRegistry, sessionsHolder);
        getTokenHandler = new GetTokenHandler(props, fileManager, userRegistry, sessionsHolder);
        loadProfileHandler = new LoadProfileHandler(props, fileManager, userRegistry, sessionsHolder);
        saveProfileHandler = new SaveProfileHandler(props, fileManager, userRegistry, sessionsHolder);
        hardwareHandler = new HardwareHandler(props, fileManager, userRegistry, sessionsHolder);
        pingHandler = new PingHandler(props, fileManager, userRegistry, sessionsHolder);
        tweetHandler = new TweetHandler(props, fileManager, userRegistry, sessionsHolder, new TwitterWrapper());
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new ClientChannelStateHandler(sessionsHolder));

        if (sslCtx != null) {
            SslHandler sslHandler = sslCtx.newHandler(ch.alloc());
            pipeline.addLast(sslHandler);
        }

        //process input
        pipeline.addLast(new ReplayingMessageDecoder(stats));

        //process output
        pipeline.addLast(new DeviceMessageEncoder());

        //business logic
        pipeline.addLast(registerHandler);
        pipeline.addLast(loginHandler);
        pipeline.addLast(getTokenHandler);
        pipeline.addLast(loadProfileHandler);
        pipeline.addLast(saveProfileHandler);
        pipeline.addLast(hardwareHandler);
        pipeline.addLast(pingHandler);
        pipeline.addLast(tweetHandler);
    }
}

