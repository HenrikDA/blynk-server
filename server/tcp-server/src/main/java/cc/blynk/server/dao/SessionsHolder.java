package cc.blynk.server.dao;

import cc.blynk.server.model.auth.ChannelState;
import cc.blynk.server.model.auth.Session;
import cc.blynk.server.model.auth.User;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds session info related to specific user.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/18/2015.
 */
public class SessionsHolder {

    private static final Logger log = LogManager.getLogger(SessionsHolder.class);

    private Map<User, Session> userSession = new ConcurrentHashMap<>();

    public void addAppChannelToGroup(User user, Channel channel, int msgId) {
        Session session = getSessionByUser(user);
        session.addAppChannel(channel, msgId);
    }

    public void addHardwareChannelToGroup(User user, Channel channel, int msgId) {
        Session session = getSessionByUser(user);
        session.addHardwareChannel(channel, msgId);
    }

    public void removeFromSession(ChannelState channel) {
        Session session = userSession.get(channel.user);
        if (session != null) {
            session.remove(channel);
        }
    }

    public Map<User, Session> getUserSession() {
        return userSession;
    }

    //todo synchronized?
    private Session getSessionByUser(User user) {
        Session group = userSession.get(user);
        //only one side came
        if (group == null) {
            log.trace("Creating unique session for user: {}", user);
            group = new Session();
            userSession.put(user, group);
        }

        return group;
    }

}