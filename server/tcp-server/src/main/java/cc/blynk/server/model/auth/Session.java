package cc.blynk.server.model.auth;

import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.server.exceptions.DeviceNotInNetworkException;
import cc.blynk.server.exceptions.UserAlreadyLoggedIn;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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

    //todo for now - simplest possible implementation
    //todo expect right n
    public static List<ChannelFuture> sendMessageTo(MessageBase message, Set<Channel> channels) {
        if (channels.size() == 0) {
            throw new DeviceNotInNetworkException("No device in session.", message.id);
        }
        List<ChannelFuture> futureList = new ArrayList<>();
        for (Channel channel : channels) {
            log.trace("Sending {} to {}", message, channel);
            futureList.add(channel.writeAndFlush(message));
        }
        return futureList;
    }

    //todo not sure, but netty processes same channel in same thread, so no sync
    public void addAppChannel(Channel channel, int msgId) {
        //if login from same channel again - do not allow.
        if (appChannels.contains(channel)) {
            throw new UserAlreadyLoggedIn("User already logged. Client problem. CHECK!", msgId);
        }
        appChannels.add(channel);
    }

    //todo not sure, but netty processes same channel in same thread, so no sync
    public void addHardwareChannel(Channel channel, int msgId) {
        //if login from same channel again - do not allow.
        if (hardwareChannels.contains(channel)) {
            throw new UserAlreadyLoggedIn("User already logged. Client problem. CHECK!", msgId);
        }
        hardwareChannels.add(channel);
    }

    public Set<Channel> getAppChannels() {
        return appChannels;
    }

    public Set<Channel> getHardwareChannels() {
        return hardwareChannels;
    }

    public boolean isFromHardware(Channel channel) {
        return hardwareChannels.contains(channel);
    }

    public boolean isFromApp(Channel channel) {
        return appChannels.contains(channel);
    }

    public List<ChannelFuture> sendMessageToHardware(MessageBase message) {
        return sendMessageTo(message, hardwareChannels);
    }

    public void remove(ChannelState channelServer) {
        if (channelServer.isHardwareChannel) {
            hardwareChannels.remove(channelServer);
        } else {
            appChannels.remove(channelServer);
        }
    }

    public int hardwareSize() {
        return hardwareChannels.size();
    }

    public int appSize() {
        return appChannels.size();
    }

}
