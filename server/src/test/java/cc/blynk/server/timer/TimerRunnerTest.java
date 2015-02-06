package cc.blynk.server.timer;

import cc.blynk.server.auth.User;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.group.Session;
import cc.blynk.server.group.SessionsHolder;
import cc.blynk.server.model.UserProfile;
import cc.blynk.server.model.Widget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.when;

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

    @Mock
    private TimerRunner timerRunner;

    @Mock
    private User user;

    @Mock
    private UserProfile userProfile;

    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    private ConcurrentHashMap<User, Session> userSession = new ConcurrentHashMap<>();

    private Set<Widget> timers = new HashSet<>();

    @Before
    public void init() {
        Widget w = new Widget();
        w.setStartTime(100000L);
        w.setValue("AAAA");
        timers.add(w);
    }

    @Test
    public void testTimer() {
        int userCount = 10_000;
        for (int i = 0; i < userCount; i++) {
            users.put(String.valueOf(i), user);
        }

        when(userRegistry.getUsers()).thenReturn(users);
        when(sessionsHolder.getUserSession()).thenReturn(userSession);
        when(user.getUserProfile()).thenReturn(userProfile);
        when(userProfile.getDashboardTimerWidgets()).thenReturn(timers);

        timerRunner = new TimerRunner(userRegistry, sessionsHolder);
        //when(timerRunner.timerTick(any(), 10000L)).thenReturn(false);

        timerRunner.run();

    }

}
