package cc.blynk.client;

import static cc.blynk.common.enums.Command.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 * Convertor between user-friendly command and protocol command code
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
            case "activate" :
                return ACTIVATE_DASHBOARD;
            case "deactivate" :
                return DEACTIVATE_DASHBOARD;
            case "register" :
                return REGISTER;
            case "tweet" :
                return TWEET;

            default:
                throw new IllegalArgumentException("Unsupported command");
        }
    }

}
