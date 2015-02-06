package cc.blynk.server.timer;

import cc.blynk.server.auth.User;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.model.DashBoard;
import cc.blynk.server.model.Widget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/6/2015.
 *
 * Simplest possible implementation.
 *
 * //todo optimize if needed
 */
public class TimerRunner implements Runnable {

    private static final Logger log = LogManager.getLogger(TimerRunner.class);

    private final UserRegistry userRegistry;
    private int counter;

    public TimerRunner(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        //millis we need to wait to start scheduler at the beginning of a second.
        long startDelay = 1000 - (System.currentTimeMillis() % 1000);
        scheduler.scheduleAtFixedRate(this, startDelay, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        log.debug("Starting timer...");
        counter = 0;
        for (User user : userRegistry.getUsers().values()) {
            if (user.getUserProfile().getDashBoards() != null) {
                for (DashBoard dashBoard : user.getUserProfile().getDashBoards()) {
                    makeAction(dashBoard.getTimerWidgets());
                }
            }
        }
        log.debug("Timer finished. Processed {} timers.", counter);
    }

    private void makeAction(Set<Widget> timerWidgets) {
        for (Widget timer : timerWidgets) {
            counter++;
        }
    }

    private void process(Widget timer) {

    }
}
