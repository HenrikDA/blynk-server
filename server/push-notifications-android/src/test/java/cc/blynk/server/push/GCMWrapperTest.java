package cc.blynk.server.push;

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/8/2015.
 */
public class GCMWrapperTest {

    @Test
    @Ignore
    public void testSendMessage() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("Hello", "World");
        new GCMWrapper().sendMessage("534534534534", payload);
        Thread.sleep(1000);
    }

}
