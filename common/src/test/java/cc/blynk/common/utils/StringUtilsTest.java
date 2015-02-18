package cc.blynk.common.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/18/2015.
 */
public class StringUtilsTest {

    @Test
    public void testCorrectFastSplit() {
        String in = "ar 1 2 3 4 5 6".replaceAll(" ", "\0");

        String[] res = StringUtils.split(in, '\0');
        assertEquals(7, res.length);
        assertEquals("ar", res[0]);
        assertEquals("1", res[1]);
        assertEquals("2", res[2]);
        assertEquals("3", res[3]);
        assertEquals("4", res[4]);
        assertEquals("5", res[5]);
        assertEquals("6", res[6]);
    }

    @Test
    public void testCorrectFastNewSplit() {
        String in = "ar 1 2 3 4 5 6".replaceAll(" ", "\0");

        String res = StringUtils.split(in);
        assertNotNull(res);
        assertEquals("1", res);


        in = "ar 22222".replaceAll(" ", "\0");
        res = StringUtils.split(in);
        assertNotNull(res);
        assertEquals("22222", res);
    }

}
