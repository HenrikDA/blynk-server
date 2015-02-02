package cc.blynk.server.auth;

import java.nio.channels.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ddumanskiy
 * Date: 6/16/13
 * Time: 9:52 AM
 */
public class Session {

    public static Map<Channel, User> channelToken = new ConcurrentHashMap<>();

}
