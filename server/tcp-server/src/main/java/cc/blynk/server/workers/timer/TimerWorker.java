package cc.blynk.server.workers.timer;

import cc.blynk.common.model.messages.protocol.HardwareMessage;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.exceptions.DeviceNotInNetworkException;
import cc.blynk.server.model.auth.Session;
import cc.blynk.server.model.auth.User;
import cc.blynk.server.model.widgets.others.Timer;
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
 * //todo optimize!!!
 */
public class TimerWorker implements Runnable {

    private static final Logger log = LogManager.getLogger(TimerWorker.class);

    private UserRegistry userRegistry;
    private SessionsHolder sessionsHolder;
    private ZoneId UTC = ZoneId.of("UTC");

    private int allTimers;
    private int tickedTimers;
    private int onlineTimers;

    protected TimerWorker() {
    }

    public TimerWorker(UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        this.userRegistry = userRegistry;
        this.sessionsHolder = sessionsHolder;
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
        allTimers = 0;
        tickedTimers = 0;
        onlineTimers = 0;

        LocalDateTime localDateTime = LocalDateTime.now(UTC);

        long curTime = localDateTime.getSecond() + localDateTime.getMinute() * 60 + localDateTime.getHour() * 3600;

        for (User user : userRegistry.getUsers().values()) {
            if (user.getUserProfile().getDashBoards() != null) {
                for (Timer timer : user.getUserProfile().getDashboardTimerWidgets()) {
                    allTimers++;
                    sendMessageIfTicked(user, curTime, timer.startTime, timer.startValue);
                    sendMessageIfTicked(user, curTime, timer.stopTime, timer.stopValue);
                }
            }
        }
        log.debug("Timer finished. Processed {}/{}/{} timers.", onlineTimers, tickedTimers, allTimers);
    }

    private void sendMessageIfTicked(User user, long curTime, Long time, String value) {
        if (timerTick(curTime, time)) {
            tickedTimers++;
            Session session = sessionsHolder.getUserSession().get(user);
            if (session != null) {
                onlineTimers++;
                try {
                    session.sendMessageToHardware(new HardwareMessage(7777, value));
                } catch (DeviceNotInNetworkException e) {
                    log.warn("Timer send for user {} failed. No Device in Network.", user.getName());
                }
            }
        }
    }

    protected boolean timerTick(long curTime, Long timerStart) {
        if (timerStart == null) {
            log.error("Timer start field is empty. Shouldn't happen. REPORT!");
            return false;
        }

        return curTime == timerStart;
    }

}
