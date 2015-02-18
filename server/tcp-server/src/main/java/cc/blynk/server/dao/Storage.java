package cc.blynk.server.dao;

import cc.blynk.server.model.auth.User;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/18/2015.
 */
public interface Storage {

    public void store(User user, Integer dashId, String body, int msgId);

}
