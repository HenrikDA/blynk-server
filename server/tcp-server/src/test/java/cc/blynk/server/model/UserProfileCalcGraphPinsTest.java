package cc.blynk.server.model;

import cc.blynk.server.utils.JsonParser;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/17/2015.
 */
public class UserProfileCalcGraphPinsTest {

    @Test
    public void testHas1Pin() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json.txt");

        UserProfile userProfile = JsonParser.parseProfile(is);
        String userProfileString = userProfile.toString();

        assertNotNull(userProfileString);
        assertTrue(userProfileString.contains("dashBoards"));

        userProfile.calcGraphPins();

        assertTrue(userProfile.hasGraphPin(1, (byte) 8));
    }

    @Test
    public void testNoPins() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json_4.txt");

        UserProfile userProfile = JsonParser.parseProfile(is);
        String userProfileString = userProfile.toString();

        assertNotNull(userProfileString);
        assertTrue(userProfileString.contains("dashBoards"));

        userProfile.calcGraphPins();

        assertFalse(userProfile.hasGraphPin(1, (byte) 8));
    }

    @Test
    public void testManyPins() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json_5.txt");

        UserProfile userProfile = JsonParser.parseProfile(is);
        String userProfileString = userProfile.toString();

        assertNotNull(userProfileString);
        assertTrue(userProfileString.contains("dashBoards"));

        userProfile.calcGraphPins();

        assertTrue(userProfile.hasGraphPin(1, (byte) 8));
        assertTrue(userProfile.hasGraphPin(1, (byte) 9));


        assertFalse(userProfile.hasGraphPin(2, (byte) 9));
        assertTrue(userProfile.hasGraphPin(2, (byte) 8));
        assertTrue(userProfile.hasGraphPin(2, (byte) 2));
    }

}
