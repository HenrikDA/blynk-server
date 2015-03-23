package cc.blynk.common.model.messages;

import cc.blynk.common.enums.Command;
import cc.blynk.common.exceptions.BaseServerException;
import cc.blynk.common.exceptions.UnsupportedCommandException;
import cc.blynk.common.model.messages.protocol.HardwareMessage;
import cc.blynk.common.model.messages.protocol.PingMessage;
import cc.blynk.common.model.messages.protocol.appllication.*;
import cc.blynk.common.model.messages.protocol.hardware.TweetMessage;

import static cc.blynk.common.enums.Command.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class MessageFactory {

    public static Message produce(int messageId, short command, String body) {
        switch (command) {
            case HARDWARE_COMMAND :
                return new HardwareMessage(messageId, body);
            case PING :
                return new PingMessage(messageId, body);
            case SAVE_PROFILE :
                return new SaveProfileMessage(messageId, body);
            case LOAD_PROFILE :
                return new LoadProfileMessage(messageId, body);
            case ACTIVATE_DASHBOARD :
                return new ActivateDashboardMessage(messageId, body);
            case DEACTIVATE_DASHBOARD :
                return new DeActivateDashboardMessage(messageId, body);
            case GET_TOKEN :
                return new GetTokenMessage(messageId, body);
            case REFRESH_TOKEN :
                return new RefreshTokenMessage(messageId, body);
            case LOGIN :
                return new LoginMessage(messageId, body);
            case TWEET :
                return new TweetMessage(messageId, body);
            case REGISTER :
                return new RegisterMessage(messageId, body);

            default: throw new UnsupportedCommandException(String.format("Command with code %d not supported message.", command), messageId);
        }
    }

    public static ResponseMessage produce(int messageId, int responseCode) {
        return new ResponseMessage(messageId, Command.RESPONSE, responseCode);
    }

    public static ResponseMessage produce(BaseServerException exception) {
        return new ResponseMessage(exception.msgId, Command.RESPONSE, exception.errorCode);
    }

}
