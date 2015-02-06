package cc.blynk.server.timer;

import cc.blynk.common.model.messages.protocol.HardwareMessage;
import cc.blynk.server.auth.User;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.group.Session;
import cc.blynk.server.group.SessionsHolder;
import cc.blynk.server.model.Widget;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/6/2015.
 *
 * Simplest possible timer implementation.
 *
 * //todo optimize if needed
 */
public class TimerRunner implements Runnable {

    private static final Logger log = LogManager.getLogger(TimerRunner.class);

    private final UserRegistry userRegistry;
    private final SessionsHolder sessionsHolder;
    private ZoneId UTC = ZoneId.of("UTC");

    public TimerRunner(UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        this.userRegistry = userRegistry;
        this.sessionsHolder = sessionsHolder;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        //millis we need to wait to start scheduler at the beginning of a second.
        long startDelay = 1001 - (System.currentTimeMillis() % 1000);
        scheduler.scheduleAtFixedRate(this, startDelay, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        log.debug("Starting timer...");
        int counter = 0;
        LocalDateTime localDateTime = LocalDateTime.now(UTC);

        long curTime = localDateTime.getSecond() + localDateTime.getMinute() * 60 + localDateTime.getHour() * 3600;

        for (User user : userRegistry.getUsers().values()) {
            if (user.getUserProfile().getDashBoards() != null) {
                for (Widget timer : user.getUserProfile().getDashboardTimerWidgets()) {
                    counter++;
                    if (timerTick(curTime, timer.getStartTime())) {
                        Session session = sessionsHolder.getUserSession().get(user);
                        if (session != null) {
                            session.sendMessageToHardware(new HardwareMessage(0, timer.getValue()));
                        }
                    }
                }
            }
        }
        log.debug("Timer finished. Processed {} timers.", counter);
    }

    protected boolean timerTick(long curTime, Long timerStart) {
        if (timerStart == null) {
            log.error("Timer start field is empty. Shouldn't happen. REPORT!");
            return false;
        }

        return curTime == timerStart;
    }

}
