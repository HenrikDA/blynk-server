package cc.blynk.server;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.common.utils.Config;
import cc.blynk.common.utils.ParseUtil;
import cc.blynk.common.utils.PropertiesUtil;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.workflow.BaseSimpleChannelInboundHandler;
import cc.blynk.server.utils.JsonParser;
import cc.blynk.server.workers.ProfileSaverWorker;
import cc.blynk.server.workers.PropertiesChangeWatcherWorker;
import cc.blynk.server.workers.timer.TimerWorker;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Properties;

/**
 * Entry point for server launch.
 *
 * By default starts 2 servers on different ports.
 * First is plain tcp/ip sockets server for hardware, second tls/ssl tcp/ip server for mobile applications.
 *
 * In addition launcher start all related to business logic threads like saving user profiles thread, timers
 * processing thread, properties reload thread and so on.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/16/2015.
 */
public class Launcher {

    private final Logger log = LogManager.getLogger(Launcher.class);

    public static void main(String[] args) throws Exception {
        Properties serverProperties = PropertiesUtil.loadProperties(Config.SERVER_PROPERTIES_FILENAME);
        //configurable folder for logs via property.
        System.setProperty("logs.folder", serverProperties.getProperty("logs.folder"));

        new Launcher().launch(args, serverProperties);
    }

    public void launch(String[] args, Properties serverProperties) throws Exception {
        //just to init mapper on server start and not first access
        JsonParser.check();

        processArguments(args, serverProperties);

        boolean sslEnabled = PropertiesUtil.getBoolProperty(serverProperties, "app.ssl.enabled");


        FileManager fileManager = new FileManager(serverProperties.getProperty("data.folder"));
        SessionsHolder sessionsHolder = new SessionsHolder();

        log.debug("Reading user DB.");
        UserRegistry userRegistry = new UserRegistry(fileManager.deserialize());
        log.debug("Reading user DB finished.");
        GlobalStats stats = new GlobalStats();

        new TimerWorker(userRegistry, sessionsHolder).start();
        ProfileSaverWorker profileSaverWorker = new ProfileSaverWorker(userRegistry, fileManager,
                PropertiesUtil.getIntProperty(serverProperties, "profile.save.worker.period"), stats);
        profileSaverWorker.start();

        Server server = new Server(serverProperties, fileManager, userRegistry, sessionsHolder, stats);

        Server sslServer = null;
        if (sslEnabled) {
            sslServer = new SSLServer(serverProperties, fileManager, userRegistry, sessionsHolder, stats);
            log.info("SSL for app. enabled.");
            new Thread(sslServer).start();
        }

        List<BaseSimpleChannelInboundHandler> baseHandlers = server.getHandlersHolder().getBaseHandlers();
        if (sslServer != null) {
            baseHandlers.addAll(sslServer.getHandlersHolder().getBaseHandlers());
        }

        new Thread(new PropertiesChangeWatcherWorker(Config.SERVER_PROPERTIES_FILENAME, baseHandlers)).start();

        new Thread(server).start();

        addShutDownHook(server, sslServer, profileSaverWorker);
    }

    //todo test it works...
    private void addShutDownHook(final Server server, final Server sslServer, final ProfileSaverWorker profileSaverWorker) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Catch shutdown hook. Trying to save users and close threads.");
                profileSaverWorker.run();
                server.stop();
                if (sslServer != null) {
                    sslServer.stop();
                }
            }
        });
    }

    private void processArguments(String[] args, Properties serverProperties) throws ParseException {
        Options options = new Options();
        options.addOption("port", true, "Server port.");
        options.addOption("sslPort", true, "Server SSL port.");
        CommandLine cmd = new BasicParser().parse(options, args);

        String portString = cmd.getOptionValue("port");
        String sslPortString = cmd.getOptionValue("sslPort");

        if (portString != null) {
            ParseUtil.parseInt(portString);
            serverProperties.put("server.default.port", portString);
        }
        if (sslPortString != null) {
            ParseUtil.parseInt(sslPortString);
            serverProperties.put("server.ssl.port", sslPortString);
        }
    }

}
