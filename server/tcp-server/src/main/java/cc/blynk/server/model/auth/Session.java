package cc.blynk.server.model.auth;

import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.server.exceptions.DeviceNotInNetworkException;
import cc.blynk.server.exceptions.UserAlreadyLoggedIn;
import cc.blynk.server.model.auth.nio.ChannelState;
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

    public final Set<Channel> appChannels = new ConcurrentSet<>();
    public final Set<Channel> hardwareChannels = new ConcurrentSet<>();

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

    public List<ChannelFuture> sendMessageToHardware(Integer activeDashId, MessageBase message) {
        List<ChannelFuture> futureList = new ArrayList<>();
        for (Channel channel : hardwareChannels) {
            Integer dashId = ((ChannelState) channel).dashId;
            if (dashId.equals(activeDashId)) {
                log.trace("Sending {} to {}", message, channel);
                futureList.add(channel.writeAndFlush(message));
            }
        }
        return futureList;
    }

    public void addChannel(ChannelState channel, int msgId) {
        if (channel.isHardwareChannel) {
            addChannel(hardwareChannels, channel, msgId);
        } else {
            addChannel(appChannels, channel, msgId);
        }
    }

    //todo not sure, but netty processes same channel in same thread, so no sync
    private void addChannel(Set<Channel> channelSet, Channel channel, int msgId) {
        //if login from same channel again - do not allow.
        if (channelSet.contains(channel)) {
            throw new UserAlreadyLoggedIn("User already logged. Client problem. CHECK!", msgId);
        }
        channelSet.add(channel);
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

}
