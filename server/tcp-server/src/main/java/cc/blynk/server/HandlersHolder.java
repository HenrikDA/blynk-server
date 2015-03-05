package cc.blynk.server;

import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.auth.LoginHandler;
import cc.blynk.server.handlers.auth.RegisterHandler;
import cc.blynk.server.handlers.workflow.*;
import cc.blynk.server.twitter.TwitterWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/27/2015.
 */
public class HandlersHolder {

    //sharable handlers
    ClientChannelStateHandler clientChannelStateHandler;
    RegisterHandler registerHandler;
    LoginHandler loginHandler;
    GetTokenHandler getTokenHandler;
    LoadProfileHandler loadProfileHandler;
    SaveProfileHandler saveProfileHandler;
    HardwareHandler hardwareHandler;
    PingHandler pingHandler;
    TweetHandler tweetHandler;

    public HandlersHolder(ServerProperties props, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        clientChannelStateHandler = new ClientChannelStateHandler(sessionsHolder);
        registerHandler = new RegisterHandler(fileManager, userRegistry, sessionsHolder);
        loginHandler = new LoginHandler(fileManager, userRegistry, sessionsHolder);
        getTokenHandler = new GetTokenHandler(props, fileManager, userRegistry, sessionsHolder);
        loadProfileHandler = new LoadProfileHandler(props, fileManager, userRegistry, sessionsHolder);
        saveProfileHandler = new SaveProfileHandler(props, fileManager, userRegistry, sessionsHolder);
        hardwareHandler = new HardwareHandler(props, fileManager, userRegistry, sessionsHolder);
        pingHandler = new PingHandler(props, fileManager, userRegistry, sessionsHolder);
        tweetHandler = new TweetHandler(props, fileManager, userRegistry, sessionsHolder, new TwitterWrapper());
    }

    //needed only for reloadable properties
    public List<BaseSimpleChannelInboundHandler> getBaseHandlers() {
        return new ArrayList<BaseSimpleChannelInboundHandler>() {
           {
               add(getTokenHandler);
               add(loadProfileHandler);
               add(saveProfileHandler);
               add(hardwareHandler);
               add(pingHandler);
               add(tweetHandler);
            }
        };
    }

}
