package cc.blynk.common.model.messages.protocol;

import cc.blynk.common.model.messages.Message;

import static cc.blynk.common.enums.Command.LOGIN;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class LoginMessage extends Message {

    public LoginMessage(int messageId, String body) {
        super(messageId, LOGIN, body.length(), body);
    }

    @Override
    public String toString() {
        return "LoginMessage{" + super.toString() + "}";
    }
}
