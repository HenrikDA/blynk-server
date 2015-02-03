package cc.blynk.server;

import cc.blynk.common.handlers.decoders.ReplayingMessageDecoder;
import cc.blynk.common.handlers.encoders.DeviceMessageEncoder;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.handlers.*;
import cc.blynk.server.utils.FileManager;
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

    public ServerHandlersInitializer(FileManager fileManager, UserRegistry userRegistry) {
        this.fileManager = fileManager;
        this.userRegistry = userRegistry;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //process input
        pipeline.addLast(new ReplayingMessageDecoder());

        //process output
        pipeline.addLast(new DeviceMessageEncoder());

        //business logic
        pipeline.addLast(new RegisterHandler(fileManager, userRegistry));
        pipeline.addLast(new LoginHandler(fileManager, userRegistry));
        pipeline.addLast(new GetTokenHandler(fileManager, userRegistry));
        pipeline.addLast(new LoadProfileHandler(fileManager, userRegistry));
        pipeline.addLast(new SaveProfileHandler(fileManager, userRegistry));
        pipeline.addLast(new HardwareHandler(fileManager, userRegistry));
        pipeline.addLast(new PingHandler(fileManager, userRegistry));
    }
}

