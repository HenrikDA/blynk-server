package cc.blynk.server;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.common.utils.Config;
import cc.blynk.common.utils.ParseUtil;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.core.BaseServer;
import cc.blynk.server.core.application.AppServer;
import cc.blynk.server.core.hardware.HardwareServer;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.JedisWrapper;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.workflow.BaseSimpleChannelInboundHandler;
import cc.blynk.server.model.auth.User;
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
import java.util.Map;
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
public class ServerLauncher {

    private final static Logger log = LogManager.getLogger(ServerLauncher.class);

    private final static Options options = new Options();

    static {
        options.addOption("hardPort", true, "Hardware server port.")
               .addOption("appPort", true, "Application server port.")
               .addOption("workerThreads", true, "Server worker threads.")
               .addOption("disableAppSsl", false, "Disables SSL for app mode.");
    }

    public static void main(String[] args) throws Exception {
        ServerProperties serverProperties = new ServerProperties();
        //configurable folder for logs via property.
        System.setProperty("logs.folder", serverProperties.getProperty("logs.folder"));

        new ServerLauncher().launch(args, serverProperties);
    }

    public void launch(String[] args, ServerProperties serverProperties) throws Exception {
        //just to init mapper on server start and not first access
        JsonParser.check();

        processArguments(args, serverProperties);

        FileManager fileManager = new FileManager(serverProperties.getProperty("data.folder"));
        SessionsHolder sessionsHolder = new SessionsHolder();

        JedisWrapper jedisWrapper = new JedisWrapper(serverProperties);

        log.debug("Starting reading user DB.");
        //first reading all data from disk
        Map<String, User> users = fileManager.deserialize();
        //after that getting full DB from Redis and adding here.
        users.putAll(jedisWrapper.getAllUsersDB());
        //todo save all to disk to have latest version locally???

        UserRegistry userRegistry = new UserRegistry(users);
        log.debug("Reading user DB finished.");

        GlobalStats stats = new GlobalStats();

        new TimerWorker(userRegistry, sessionsHolder).start();

        ProfileSaverWorker profileSaverWorker = new ProfileSaverWorker(jedisWrapper, userRegistry, fileManager,
                serverProperties.getIntProperty("profile.save.worker.period"), stats);
        profileSaverWorker.start();

        HardwareServer hardwareServer = new HardwareServer(serverProperties, fileManager, userRegistry, sessionsHolder, stats);
        AppServer appServer = new AppServer(serverProperties, fileManager, userRegistry, sessionsHolder, stats);

        List<BaseSimpleChannelInboundHandler> baseHandlers = hardwareServer.getBaseHandlers();
        baseHandlers.addAll(appServer.getBaseHandlers());

        new Thread(new PropertiesChangeWatcherWorker(Config.SERVER_PROPERTIES_FILENAME, baseHandlers)).start();

        new Thread(appServer).start();
        new Thread(hardwareServer).start();

        addShutDownHook(hardwareServer, appServer, profileSaverWorker);
    }

    //todo test it works...
    private void addShutDownHook(final BaseServer server, final BaseServer appServer, final ProfileSaverWorker profileSaverWorker) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Catch shutdown hook. Trying to save users and close threads.");
                profileSaverWorker.run();
                server.stop();
                if (appServer != null) {
                    appServer.stop();
                }
            }
        });
    }

    private void processArguments(String[] args, Properties serverProperties) throws ParseException {
        CommandLine cmd = new BasicParser().parse(options, args);

        String hardPort = cmd.getOptionValue("hardPort");
        String appPort = cmd.getOptionValue("appPort");
        String workerThreadsString = cmd.getOptionValue("workerThreads");

        if (hardPort != null) {
            ParseUtil.parseInt(hardPort);
            serverProperties.put("server.default.port", hardPort);
        }
        if (appPort != null) {
            ParseUtil.parseInt(appPort);
            serverProperties.put("server.ssl.port", appPort);
        }
        if (workerThreadsString != null) {
            ParseUtil.parseInt(workerThreadsString);
            serverProperties.put("server.worker.threads", workerThreadsString);
        }
    }

}
