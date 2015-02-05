package cc.blynk.server.group;

import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.server.exceptions.DeviceNotInNetworkException;
import cc.blynk.server.exceptions.UserAlreadyLoggedIn;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ConcurrentSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 * 
 * DefaultChannelGroup.java too complicated. so doing in simple way for now.
 * 
 */
public class Session {

    private static final Logger log = LogManager.getLogger(Session.class);

    private final Set<Channel> appChannels = new ConcurrentSet<>();
    private final Set<Channel> hardwareChannels = new ConcurrentSet<>();

    //todo not sure, but netty processes same channel in same thread, so no sync
    protected void addAppChannel(Channel channel, int msgId) {
        //if login from same channel again - do not allow.
        if (appChannels.contains(channel)) {
            throw new UserAlreadyLoggedIn("User already logged. Client problem. CHECK!", msgId);
        }
        appChannels.add(channel);
    }

    //todo not sure, but netty processes same channel in same thread, so no sync
    protected void addHardwareChannel(Channel channel, int msgId) {
        //if login from same channel again - do not allow.
        if (hardwareChannels.contains(channel)) {
            throw new UserAlreadyLoggedIn("User already logged. Client problem. CHECK!", msgId);
        }
        hardwareChannels.add(channel);
    }

    public Set<Channel> getAppChannels() {
        return appChannels;
    }

    //todo for now - simplest possible implementation
    //todo expect right n
    public List<ChannelFuture> sendMessageToHardware(final ChannelHandlerContext ctx, MessageBase message) {
        if (hardwareSize() == 0) {
            throw new DeviceNotInNetworkException("No devices in session.", message.id);
        }
        List<ChannelFuture> futureList = new ArrayList<>();
        for (Channel hardwareChannel : hardwareChannels) {
            log.debug("Sending {} to {}", message, hardwareChannel);
            futureList.add(hardwareChannel.writeAndFlush(message));
        }
        return futureList;
    }

    public int hardwareSize() {
        return hardwareChannels.size();
    }

    public int appSize() {
        return appChannels.size();
    }

}
