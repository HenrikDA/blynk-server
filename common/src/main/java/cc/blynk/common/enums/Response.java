package cc.blynk.common.enums;

import cc.blynk.common.utils.ReflectionUtil;

import java.util.Map;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public final class Response {

    public static final int OK = 200;
    public static final int INVALID_COMMAND_FORMAT = 2;
    public static final int USER_NOT_REGISTERED = 3;
    public static final int USER_ALREADY_REGISTERED = 4;
    public static final int USER_NOT_AUTHENTICATED = 5;
    public static final int NOT_ALLOWED = 6;
    public static final int DEVICE_NOT_IN_NETWORK = 7;
    public static final int NOT_SUPPORTED_COMMAND = 8;
    public static final int INVALID_TOKEN = 9;
    public static final int SERVER_ERROR = 10;



    //all this code just to make logging more user-friendly
    private static Map<Integer, String> valuesName = ReflectionUtil.generateMapOfValueNameInteger(Response.class);

    public static String getNameByValue(int val) {
        return valuesName.get(val);
    }
    //--------------------------------------------------------
}
