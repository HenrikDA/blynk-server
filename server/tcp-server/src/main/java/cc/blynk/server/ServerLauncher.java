package cc.blynk.server;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.common.utils.Config;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.core.application.AppServer;
import cc.blynk.server.core.hardware.HardwareServer;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.JedisWrapper;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.workflow.BaseSimpleChannelInboundHandler;
import cc.blynk.server.model.auth.User;
import cc.blynk.server.workers.ProfileSaverWorker;
import cc.blynk.server.workers.PropertiesChangeWatcherWorker;
import cc.blynk.server.workers.ShutdownHookWorker;
import cc.blynk.server.workers.timer.TimerWorker;

import java.util.List;
import java.util.Map;

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

    public static void main(String[] args) throws Exception {
        ServerProperties serverProperties = new ServerProperties();
        //configurable folder for logs via property.
        System.setProperty("logs.folder", serverProperties.getProperty("logs.folder"));

        new ArgumentsParser().processArguments(args, serverProperties);

        launch(serverProperties);
    }

    public static void launch(ServerProperties serverProperties) throws Exception {
        FileManager fileManager = new FileManager(serverProperties.getProperty("data.folder"));
        SessionsHolder sessionsHolder = new SessionsHolder();

        JedisWrapper jedisWrapper = new JedisWrapper(serverProperties);

        //first reading all data from disk
        Map<String, User> users = fileManager.deserialize();
        //after that getting full DB from Redis and adding here.
        users.putAll(jedisWrapper.getAllUsersDB());
        //todo save all to disk to have latest version locally???

        UserRegistry userRegistry = new UserRegistry(users);


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

        //todo test it works...
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHookWorker(hardwareServer, appServer, profileSaverWorker)));
    }

}
