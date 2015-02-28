package cc.blynk.server;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

import static cc.blynk.common.utils.PropertiesUtil.getIntProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class SSLServer extends Server {

    private static final Logger log = LogManager.getLogger(SSLServer.class);

    public SSLServer(Properties props, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder, GlobalStats stats) {
        this.port = getIntProperty(props, "server.ssl.port");
        this.handlersHolder = new HandlersHolder(props, fileManager, userRegistry, sessionsHolder);

        this.serverHandlersInitializer = new ServerHandlersInitializer(handlersHolder, stats,
                props.getProperty("server.ssl.cert"),
                props.getProperty("server.ssl.key"),
                props.getProperty("server.ssl.key.pass")
        );

        log.info("SSL server port {}.", port);
    }

    @Override
    public void stop() {
        log.info("Shutting down SSL server...");
        super.stop();
    }
}
