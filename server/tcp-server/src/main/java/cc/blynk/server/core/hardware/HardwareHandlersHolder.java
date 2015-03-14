package cc.blynk.server.core.hardware;

import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.auth.HardwareLoginHandler;
import cc.blynk.server.handlers.workflow.BaseSimpleChannelInboundHandler;
import cc.blynk.server.handlers.workflow.HardwareHandler;
import cc.blynk.server.handlers.workflow.PingHandler;
import cc.blynk.server.handlers.workflow.TweetHandler;
import cc.blynk.server.twitter.TwitterWrapper;
import io.netty.channel.ChannelHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/27/2015.
 */
class HardwareHandlersHolder {

    //todo cover with tests.
    //sharable handlers
    private final HardwareLoginHandler hardwareLoginHandler;
    private final HardwareHandler hardwareHandler;
    private final PingHandler pingHandler;
    private final TweetHandler tweetHandler;

    public HardwareHandlersHolder(ServerProperties props, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        this.hardwareLoginHandler = new HardwareLoginHandler(fileManager, userRegistry, sessionsHolder);
        this.hardwareHandler = new HardwareHandler(props, fileManager, userRegistry, sessionsHolder);
        this.pingHandler = new PingHandler(props, fileManager, userRegistry, sessionsHolder);
        this.tweetHandler = new TweetHandler(props, fileManager, userRegistry, sessionsHolder, new TwitterWrapper());
    }

    public List<BaseSimpleChannelInboundHandler> getBaseHandlers() {
        return new ArrayList<BaseSimpleChannelInboundHandler>() {
           {
               add(hardwareHandler);
               add(pingHandler);
               add(tweetHandler);
            }
        };
    }

    public List<ChannelHandler> getAllHandlers() {
        return new ArrayList<ChannelHandler>() {
           {
               add(hardwareLoginHandler);
               add(hardwareHandler);
               add(pingHandler);
               add(tweetHandler);
            }
        };
    }

}
