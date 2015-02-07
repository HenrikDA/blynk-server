package cc.blynk.common.model.messages;

import cc.blynk.common.enums.Command;
import cc.blynk.common.model.messages.protocol.*;

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
            case GET_TOKEN :
                return new GetTokenMessage(messageId, body);
            case LOGIN :
                return new LoginMessage(messageId, body);
            case REGISTER :
                return new RegisterMessage(messageId, body);
            case TWEET :
                return new TweetMessage(messageId, body);

            //todo app specific exception?
            default: throw new RuntimeException(String.format("Command with code %d not supported message.", command));
        }
    }

    public static ResponseMessage produce(int messageId, int responseCode) {
        return new ResponseMessage(messageId, Command.RESPONSE, responseCode);
    }

}
