package cc.blynk.client.core;

import cc.blynk.client.BaseClient;
import cc.blynk.client.handlers.ClientReplayingMessageDecoder;
import cc.blynk.common.handlers.encoders.DeviceMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.util.Random;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.03.15.
 */
public class AppClient extends BaseClient {

    protected final SslContext sslCtx;

    public AppClient(String host, int port) {
        this(host, port, new Random());
    }

    public AppClient(String host, int port, Random msgIdGenerator) {
        super(host, port, msgIdGenerator);
        log.info("Creating app client. Host {}, sslPort : {}", host, port);

        //todo think how to simplify with real certs?
        //sslCtx = SslContext.newClientContext(getFileFromResources("/test.crt"));
        try {
            this.sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } catch (SSLException e) {
            log.error("Error initializing SSL context. Reason : {}", e.getMessage());
            log.debug(e);
            throw new RuntimeException();
        }
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                pipeline.addLast(new ClientReplayingMessageDecoder());
                pipeline.addLast(new DeviceMessageEncoder());
            }
        };
    }
}
