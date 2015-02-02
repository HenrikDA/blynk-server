package cc.blynk.server.group;

import cc.blynk.common.model.messages.MessageBase;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ConcurrentSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

import static cc.blynk.common.enums.Response.DEVICE_NOT_IN_NETWORK;
import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 * 
 * DefaultChannelGroup.java too complicated. so doing in simple way for now.
 * 
 */
public class ChannelGroup {

    private static final Logger log = LogManager.getLogger(ChannelGroup.class);

    private final Set<Channel> appChannels = new ConcurrentSet<>();
    private final Set<Channel> hardwareChannels = new ConcurrentSet<>();

    protected void addAppChannel(Channel appChannel) {
        appChannels.add(appChannel);
    }

    protected void addHardwareChannel(Channel hardwareChannel) {
        hardwareChannels.add(hardwareChannel);
    }

    public Set<Channel> getAppChannels() {
        return appChannels;
    }

    //todo for now - simplest possible implementation
    public void sendMessageToHardware(final ChannelHandlerContext ctx, MessageBase message) {
        if (hardwareSize() == 0) {
            log.warn("No devices in session.");
            ctx.writeAndFlush(produce(message.id, DEVICE_NOT_IN_NETWORK));
            return;
        }
        for (Channel hardwareChannel : hardwareChannels) {
            log.debug("Sending {} to {}", message, hardwareChannel);
            ChannelFuture future = hardwareChannel.writeAndFlush(message);
            future.addListener(future1 -> {
                ctx.channel().writeAndFlush(produce(message.id, OK));
            });
        }
    }

    public int hardwareSize() {
        return hardwareChannels.size();
    }

    public int appSize() {
        return appChannels.size();
    }

}
