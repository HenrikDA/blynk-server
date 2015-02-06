package cc.blynk.server.timer;

import cc.blynk.server.auth.User;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.group.Session;
import cc.blynk.server.group.SessionsHolder;
import cc.blynk.server.model.DashBoard;
import cc.blynk.server.model.UserProfile;
import cc.blynk.server.model.Widget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/6/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class TimerRunnerTest {

    @Mock
    private UserRegistry userRegistry;

    @Mock
    private SessionsHolder sessionsHolder;

    @Spy
    @InjectMocks
    private TimerRunner timerRunner;

    @Mock
    private User user;

    @Mock
    private UserProfile userProfile;

    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    private ConcurrentHashMap<User, Session> userSession = new ConcurrentHashMap<>();

    private Set<Widget> timers = new HashSet<>();
    private Widget w = new Widget();

    @Before
    public void init() {
        timers.add(w);
    }

    @Test
    public void testTimer() {
        //wait for start of a second
        long startDelay = 1001 - (System.currentTimeMillis() % 1000);
        try {
            Thread.sleep(startDelay);
        } catch (InterruptedException e) {
        }

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("UTC"));
        long curTime = localDateTime.getSecond() + localDateTime.getMinute() * 60 + localDateTime.getHour() * 3600;
        w.setStartTime(curTime);

        int userCount = 1000;
        for (int i = 0; i < userCount; i++) {
            users.put(String.valueOf(i), user);
        }

        when(userRegistry.getUsers()).thenReturn(users);
        when(sessionsHolder.getUserSession()).thenReturn(userSession);
        when(user.getUserProfile()).thenReturn(userProfile);
        when(userProfile.getDashBoards()).thenReturn(new DashBoard[] {});
        when(userProfile.getDashboardTimerWidgets()).thenReturn(timers);

        timerRunner.run();

        verify(timerRunner, times(1000)).timerTick(eq(curTime), eq(curTime));
    }

}
