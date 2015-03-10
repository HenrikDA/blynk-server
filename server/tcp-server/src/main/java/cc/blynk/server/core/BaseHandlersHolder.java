package cc.blynk.server.core;

import cc.blynk.server.handlers.workflow.BaseSimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler;

import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/10/2015.
 */
public interface BaseHandlersHolder {

    //needed only for reloadable properties
    public List<BaseSimpleChannelInboundHandler> getBaseHandlers();

    public List<ChannelHandler> getAllHandlers();

}
