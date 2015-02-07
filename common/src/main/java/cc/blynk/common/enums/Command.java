package cc.blynk.common.enums;

import cc.blynk.common.utils.ReflectionUtil;

import java.util.Map;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public final class Command {

    public static final short RESPONSE = 0;

    //mobile client command
    public static final short REGISTER = 1;
    public static final short LOGIN = 2;
    public static final short SAVE_PROFILE = 3;
    public static final short LOAD_PROFILE = 4;
    public static final short GET_TOKEN = 5;
    public static final short PING = 6;
    public static final short GRAPH_GET = 7;
    public static final short GRAPH_LOAD = 8;
    public static final short TWEET = 12;
    //------------------------------------------

    //HARDWARE commands
    public static final short HARDWARE_COMMAND = 20;
    //------------------------------------------

    //all this code just to make logging more user-friendly
    private static Map<Short, String> valuesName = ReflectionUtil.generateMapOfValueNameShort(Command.class);

    public static String getNameByValue(short val) {
        return valuesName.get(val);
    }
    //--------------------------------------------------------

}
