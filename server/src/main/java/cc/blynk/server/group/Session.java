package cc.blynk.server.group;

import cc.blynk.server.auth.User;
import cc.blynk.server.exceptions.UserNotAuthenticated;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private static final Logger log = LogManager.getLogger(Session.class);

    private static Map<User, ChannelGroup> bridgeGroup = new ConcurrentHashMap<>();

    private static Map<Channel, User> channelToken = new ConcurrentHashMap();

    public static Map<User, ChannelGroup> getBridgeGroup() {
        return bridgeGroup;
    }

    public static void addAppChannelToGroup(User user, Channel channel) {
        ChannelGroup group = getUserGroup(user);
        group.addAppChannel(channel);
        channelToken.put(channel, user);
    }

    public static void addHardwareChannelToGroup(User user, Channel channel) {
        ChannelGroup group = getUserGroup(user);
        group.addHardwareChannel(channel);
        channelToken.put(channel, user);
    }

    //todo synchronized?
    private static ChannelGroup getUserGroup(User user) {
        ChannelGroup group = Session.bridgeGroup.get(user);
        //only one side came
        if (group == null) {
            log.info("Creating unique group for user: {}", user);
            group = new ChannelGroup();
            Session.bridgeGroup.put(user, group);
        }

        return group;
    }

    public static User findUserByChannel(Channel channel, int msgId) {
        for (Map.Entry<User, ChannelGroup> entry : bridgeGroup.entrySet()) {
            for (Channel groupChannel : entry.getValue().getAppChannels()) {
                if (groupChannel == channel) {
                    return entry.getKey();
                }
            }
        }
        throw new UserNotAuthenticated("User not logged.", msgId);
    }

}