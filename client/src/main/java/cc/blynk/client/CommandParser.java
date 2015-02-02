package cc.blynk.client;

import static cc.blynk.common.enums.Command.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class CommandParser {

    public static Short parseCommand(String stringCommand) {
        switch (stringCommand.toLowerCase()) {
            case "hardware" :
                return HARDWARE_COMMAND;
            case "ping" :
                return PING;
            case "loadprofile" :
                return LOAD_PROFILE;
            case "saveprofile" :
                return SAVE_PROFILE;
            case "gettoken" :
                return GET_TOKEN;
            case "login" :
                return LOGIN;
            case "register" :
                return REGISTER;

            default:
                throw new IllegalArgumentException("Unsupported command");
        }
    }

}
