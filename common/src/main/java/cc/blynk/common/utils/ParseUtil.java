package cc.blynk.common.utils;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/31/2015.
 */
public final class ParseUtil {

    public static int parsePortString(String portString) {
        try {
            return Integer.parseInt(portString);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Port should be an integer.");
        }
    }

}
