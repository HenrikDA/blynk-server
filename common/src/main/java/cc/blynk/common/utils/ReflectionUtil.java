package cc.blynk.common.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/4/2015.
 */
public class ReflectionUtil {

    /**
     * Used to generate map of class fields where key is field value and value is field name.
     */
    public static Map<Integer, String> generateMapOfValueNameInteger(Class<?> clazz) {
        Map<Integer, String> valuesName = new HashMap<>();
        try {
            for (Field field : clazz.getFields()) {
                valuesName.put((Integer) field.get(int.class), field.getName());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return valuesName;
    }

    /**
     * Used to generate map of class fields where key is field value and value is field name.
     */
    public static Map<Short, String> generateMapOfValueNameShort(Class<?> clazz) {
        Map<Short, String> valuesName = new HashMap<>();
        try {
            for (Field field : clazz.getFields()) {
                valuesName.put((Short) field.get(short.class), field.getName());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return valuesName;
    }

}
