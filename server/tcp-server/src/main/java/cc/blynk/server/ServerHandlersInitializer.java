package cc.blynk.server;

import cc.blynk.common.handlers.decoders.ReplayingMessageDecoder;
import cc.blynk.common.handlers.encoders.DeviceMessageEncoder;
import cc.blynk.common.stats.GlobalStats;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import java.io.File;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class ServerHandlersInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger log = LogManager.getLogger(ServerHandlersInitializer.class);

    //sharable handlers
    private HandlersHolder handlersHolder;
    private GlobalStats stats;
    private SslContext sslCtx;

    public ServerHandlersInitializer(HandlersHolder handlersHolder, GlobalStats stats) {
        this.handlersHolder = handlersHolder;
        this.stats = stats;
    }

    public ServerHandlersInitializer(HandlersHolder handlersHolder, GlobalStats stats, String serverCertPath, String serverKeyPath, String keyPass) {
        this(handlersHolder, stats);
        this.sslCtx = initSslContext(new File(serverCertPath), new File(serverKeyPath), keyPass);
    }

    public static SslContext initSslContext(File serverCert, File serverKey, String keyPass) {
        try {
            //todo this is self-signed cerf. just ot simplify for now testing.
            return SslContext.newServerContext(serverCert, serverKey, keyPass);
        } catch (SSLException e) {
            log.error("Error initializing ssl context. Reason : {}", e.getMessage());
            System.exit(0);
            //todo throw?
        }
        return null;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(handlersHolder.clientChannelStateHandler);

        if (sslCtx != null) {
            SslHandler sslHandler = sslCtx.newHandler(ch.alloc());
            pipeline.addLast(sslHandler);
        }

        //process input
        pipeline.addLast(new ReplayingMessageDecoder(stats));

        //process output
        pipeline.addLast(new DeviceMessageEncoder());

        //business logic
        pipeline.addLast(handlersHolder.registerHandler);
        pipeline.addLast(handlersHolder.loginHandler);
        pipeline.addLast(handlersHolder.getTokenHandler);
        pipeline.addLast(handlersHolder.loadProfileHandler);
        pipeline.addLast(handlersHolder.saveProfileHandler);
        pipeline.addLast(handlersHolder.hardwareHandler);
        pipeline.addLast(handlersHolder.pingHandler);
        pipeline.addLast(handlersHolder.tweetHandler);
    }
}

