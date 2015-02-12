package cc.blynk.server.dao;

import cc.blynk.server.model.auth.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for holding info regarding registered users and profiles.
 *
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 4:02 PM
 */
public class UserRegistry {

    private static final Logger log = LogManager.getLogger(UserRegistry.class);
    private ConcurrentHashMap<String, User> users;
    //init user DB if possible

    public UserRegistry(ConcurrentHashMap<String, User> users) {
        //reading DB to RAM.
        this.users = users;
    }

    private static String generateNewToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public boolean isUserExists(String name) {
        return users.get(name) != null;
    }

    public User getByName(String name) {
        return users.get(name);
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }

    //todo optimize
    public User getByToken(String token) {
        for (User user : users.values()) {
            for (String userToken : user.getDashTokens().values()) {
                if (userToken.equals(token)) {
                    return user;
                }
            }
        }
        return null;
    }

    public String getToken(User user, Long dashboardId) {
        Map<Long, String> dashTokens = user.getDashTokens();
        String token = dashTokens.get(dashboardId);

        //if token not exists. generate new one
        if (token == null) {
            log.info("Token for user {} and dashId {} not generated yet.", user.getName(), dashboardId);
            token = generateNewToken();
            log.info("Generated token for user {} and dashId {} is {}.", user.getName(), dashboardId, token);
            user.getDashTokens().put(dashboardId, token);
        } else {
            log.info("Token for user {} and dashId {} generated already. Token {}", user.getName(), dashboardId, token);
        }

        return token;
    }

    public User createNewUser(String userName, String pass) {
        User newUser = new User(userName, pass);
        users.put(userName, newUser);
        return newUser;
    }

}
