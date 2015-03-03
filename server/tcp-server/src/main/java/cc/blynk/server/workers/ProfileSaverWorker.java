package cc.blynk.server.workers;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.model.auth.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/12/2015.
 */
public class ProfileSaverWorker implements Runnable {

    private static final Logger log = LogManager.getLogger(ProfileSaverWorker.class);

    //1 min
    private final UserRegistry userRegistry;
    private final FileManager fileManager;
    private final int periodInMillis;
    private final GlobalStats stats;
    private final ScheduledExecutorService scheduler;
    private long lastStart;

    public ProfileSaverWorker(UserRegistry userRegistry, FileManager fileManager, int periodInMillis, GlobalStats stats) {
        this.userRegistry = userRegistry;
        this.fileManager = fileManager;
        this.periodInMillis = periodInMillis;
        this.stats = stats;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.lastStart = System.currentTimeMillis();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this, 1000, periodInMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        log.debug("Starting saving user db.");
        int count = 0;
        long newStart = System.currentTimeMillis();

        for (User user : userRegistry.getUsers().values()) {
            if (lastStart <= user.getLastModifiedTs()) {
                try {
                    fileManager.overrideUserFile(user);
                    count++;
                } catch (IOException e) {
                    log.error("Error saving : {}.", user);
                }
            }
        }

        lastStart = newStart;

        stats.log();
        log.debug("Saving user db finished. Modified {} users.", count);
    }


}
