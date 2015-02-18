package cc.blynk.server.dao;

import cc.blynk.common.exceptions.UnsupportedCommandException;
import cc.blynk.server.model.auth.User;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static cc.blynk.common.utils.StringUtils.split;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/18/2015.
 *
 * todo redesign. right now it is not efficient at all
 */
public class GraphInMemoryStorage implements Storage {

    private final Map<String, Queue<String>> userValues;
    private final int sizeLimit;

    public GraphInMemoryStorage(int sizeLimit) {
        this.userValues = new ConcurrentHashMap<>();
        this.sizeLimit = sizeLimit;
    }

    @Override
    public void store(User user, Integer dashId, String body, int msgId) {
        if (body.charAt(1) == 'w') {
            if (body.length() < 4) {
                throw new UnsupportedCommandException("Hardware command body too short.", msgId);
            }
            String pinString = split(body);
            Byte pin = Byte.valueOf(pinString);
            if (user.getUserProfile().hasGraphPin(dashId, pin)) {
                storeValue(user.getName(), dashId, body);
            }
        }
    }

    private void storeValue(String userName, Integer dashId, String body) {
        //expecting same user always in same thread, so no concurrency
        String key = userName + dashId;
        Queue<String> bodies = userValues.get(key);
        if (bodies == null) {
            bodies = new LinkedList<>();
            userValues.put(key, bodies);
        }

        if (bodies.size() == sizeLimit) {
            bodies.poll();
        }
        bodies.add(body);
    }

}
