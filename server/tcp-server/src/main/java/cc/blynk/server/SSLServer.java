package cc.blynk.server;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.common.utils.PropertiesUtil;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import io.netty.handler.ssl.SslContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Properties;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class SSLServer extends Server {

    private static final Logger log = LogManager.getLogger(SSLServer.class);

    private SslContext sslCtx;

    public SSLServer(Properties props, FileManager fileManager, SessionsHolder sessionsHolder, UserRegistry userRegistry, GlobalStats stats) {
        this.props = props;
        this.fileManager = fileManager;
        this.sessionsHolder = sessionsHolder;
        this.userRegistry = userRegistry;
        this.stats = stats;
        this.sslCtx = initSslContext();
        this.port = PropertiesUtil.getIntProperty(props, "server.ssl.port");
        log.info("Using SSL port : {}", port);
    }

    public static SslContext initSslContext() {
        try {
            Properties serverProperties = PropertiesUtil.loadProperties("server.properties");
            //todo this is self-signed cerf. just ot simplify for now testing.
            return SslContext.newServerContext(
                    new File(serverProperties.getProperty("server.ssl.cert")),
                    new File(serverProperties.getProperty("server.ssl.key")),
                    serverProperties.getProperty("server.ssl.key.pass"));
        } catch (SSLException e) {
            log.error("Error initializing ssl context. Reason : {}", e.getMessage(), e);
            System.exit(0);
            //todo throw?
        }
        return null;
    }

    @Override
    protected ServerHandlersInitializer getServerHandlersInitializer() {
        return new ServerHandlersInitializer(props, fileManager, userRegistry, sessionsHolder, stats, sslCtx);
    }

    @Override
    public void stop() {
        log.info("Shutting down ssl server...");
        super.stop();
        log.info("Shutting down of ssl server finished!");
    }

}
