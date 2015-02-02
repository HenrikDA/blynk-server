package cc.blynk.common.enums;

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
    //------------------------------------------

    //HARDWARE commands
    public static final short HARDWARE_COMMAND = 20;
    //------------------------------------------


}
