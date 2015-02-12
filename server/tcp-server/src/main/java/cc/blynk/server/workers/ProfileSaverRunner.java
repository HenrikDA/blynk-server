package cc.blynk.server.workers;

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
public class ProfileSaverRunner implements Runnable {

    private static final Logger log = LogManager.getLogger(ProfileSaverRunner.class);

    //1 min
    //todo move to properties
    private static final int SAVE_PERIOD = 1;

    private final UserRegistry userRegistry;
    private final FileManager fileManager;

    public ProfileSaverRunner(UserRegistry userRegistry, FileManager fileManager) {
        this.userRegistry = userRegistry;
        this.fileManager = fileManager;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this, 1, SAVE_PERIOD, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        log.debug("Starting saving user db.");
        int count = 0;
        for (User user : userRegistry.getUsers().values()) {
            try {
                fileManager.overrideUserFile(user);
                count++;
            } catch (IOException e) {
                log.error("Error saving : {}.", user);
            }
        }
        log.debug("Saving user db finished. Saved {} users.", count);
    }


}
