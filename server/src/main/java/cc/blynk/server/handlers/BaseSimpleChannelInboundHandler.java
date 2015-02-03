package cc.blynk.server.handlers;

import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.utils.FileManager;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public abstract class BaseSimpleChannelInboundHandler<I> extends SimpleChannelInboundHandler<I> {

    protected FileManager fileManager;
    protected UserRegistry userRegistry;

    public BaseSimpleChannelInboundHandler(FileManager fileManager, UserRegistry userRegistry) {
        this.fileManager = fileManager;
        this.userRegistry = userRegistry;
    }

}
