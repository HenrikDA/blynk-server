package cc.blynk.server.dao;

import cc.blynk.server.exceptions.IllegalCommandException;
import cc.blynk.server.model.UserProfile;
import cc.blynk.server.model.auth.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/26/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class GraphInMemoryStorageTest {

    private GraphInMemoryStorage storage = new GraphInMemoryStorage(1000);

    @Mock
    private User user;

    @Mock
    private UserProfile userProfile;

    @Test
    public void testNoActualStore() {
        String body = "ar 1".replaceAll(" ", "\0");
        String result = storage.store(user, 1, body, 1);

        assertNotNull(result);
        assertEquals(body, result);

    }

    @Test(expected = IllegalCommandException.class)
    public void testWrongStoreCommand() {
        String body = "aw".replaceAll(" ", "\0");
        storage.store(user, 1, body, 1);
    }

    @Test(expected = NumberFormatException.class)
    public void testStorePinNotANumber() {
        String body = "aw x".replaceAll(" ", "\0");
        storage.store(user, 1, body, 1);
    }

    @Test
    public void testStoreCorrect() {
        when(user.getUserProfile()).thenReturn(userProfile);
        when(user.getName()).thenReturn("testUserName");
        when(userProfile.hasGraphPin(1, (byte) 33)).thenReturn(true);

        String body = "aw 33".replaceAll(" ", "\0");
        String result = storage.store(user, 1, body, 1);

        assertNotNull(result);
        assertTrue(result.startsWith(body));
        //14 - ts length + '\0' separator
        assertEquals(body.length() + 14, result.length());
    }

}
