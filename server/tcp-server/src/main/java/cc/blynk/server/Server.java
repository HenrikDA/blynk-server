package cc.blynk.server;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.common.utils.PropertiesUtil;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class Server extends BaseServer {

    private static final Logger log = LogManager.getLogger(Server.class);

    public Server(Properties props, FileManager fileManager, SessionsHolder sessionsHolder, UserRegistry userRegistry, GlobalStats stats) {
        super(props, fileManager, sessionsHolder, userRegistry, stats);
        this.port = PropertiesUtil.getIntProperty(props, "server.default.port");
        log.info("Using default port : {}", port);
    }

    @Override
    public void stop() {
        log.info("Shutting down default server...");
        super.stop();
        log.info("Shutting down of default server finished!");
    }
}
