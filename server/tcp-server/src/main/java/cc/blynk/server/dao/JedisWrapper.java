package cc.blynk.server.dao;

import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.model.auth.User;
import cc.blynk.server.utils.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/4/2015.
 */
public class JedisWrapper {

    private static final Logger log = LogManager.getLogger(JedisWrapper.class);
    private final ServerProperties props;
    private boolean redisEnabled;
    private Jedis jedis;

    public JedisWrapper(ServerProperties props) {
        this.props = props;
        this.redisEnabled = props.getBoolProperty("redis.userprofile.storage.enabled");
        if (redisEnabled) {
            this.jedis = init(props);
        }
    }

    private Jedis init(ServerProperties props) {
        return new Jedis(props.getProperty("redis.userprofile.host"), props.getIntProperty("redis.userprofile.port"));
    }

    public void saveToRemoteStorage(List<User> users) {
        if (redisEnabled) {
            if (!jedis.isConnected()) {
                log.warn("Redis closed. Creating new.");
                jedis.close();
                jedis = init(props);
            }
            try {
                Pipeline pipeline = jedis.pipelined();
                for (User user : users) {
                    pipeline.set(user.getName(), user.toString());
                }
                pipeline.sync();
            } catch (JedisConnectionException e) {
                log.error("Error connecting to redis. Host {}:{}", jedis.getClient().getHost(), jedis.getClient().getPort());
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    public Map<String, User> getAllUsersDB() {
        if (redisEnabled) {
            try {
                Set<String> keys = jedis.keys("*");
                log.info("Found {} keys in redis.", keys);

                if (keys != null && keys.size() > 0) {
                    List<String> usersJson = jedis.mget(keys.toArray(new String[keys.size()]));
                    log.info("Got all user DB.");

                    Map<String, User> users = new ConcurrentHashMap<>();
                    for (String userJson : usersJson) {
                        try {
                            User user = JsonParser.parseUser(userJson);
                            if (user != null) {
                                users.putIfAbsent(user.getName(), user);
                            }
                        } catch (IOException e) {
                            log.error("Error parsing userProfile {}.", userJson);
                        }
                    }
                    return users;
                }
            } catch (JedisConnectionException e) {
                log.error("Error connecting to redis. Host {}:{}", jedis.getClient().getHost(), jedis.getClient().getPort());
            } catch (Exception e) {
                log.error(e);
            }
        }

        return Collections.emptyMap();
    }

}
