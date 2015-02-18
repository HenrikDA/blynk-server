package cc.blynk.common.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/18/2015.
 */
public class StringUtilsTest {

    @Test
    public void testCorrectFastSplit() {
        String in = "hardware 1 2 3 4 5 6".replaceAll(" ", "\0");

        String[] res = StringUtils.split(in, '\0');
        assertEquals(7, res.length);
        assertEquals("hardware", res[0]);
        assertEquals("1", res[1]);
        assertEquals("2", res[2]);
        assertEquals("3", res[3]);
        assertEquals("4", res[4]);
        assertEquals("5", res[5]);
        assertEquals("6", res[6]);
    }

}
