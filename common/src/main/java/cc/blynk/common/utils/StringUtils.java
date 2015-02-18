package cc.blynk.common.utils;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/18/2015.
 */
public class StringUtils {

    /**
     * Fast split in java. This one is ~25% faster than s.split("\0");
     * Copied from here https://gist.github.com/banthar/2923321
     *
     */
    //todo still could be optimized more.
    public static String[] split(String s, char delimeter) {
        int count = 1;

        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) == delimeter)
                count++;

        String[] array = new String[count];

        int a = -1;
        int b = 0;

        for (int i = 0; i < count; i++) {

            while (b < s.length() && s.charAt(b) != delimeter)
                b++;

            array[i] = s.substring(a + 1, b);
            a = b;
            b++;

        }

        return array;
    }

}
