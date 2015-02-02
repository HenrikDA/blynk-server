package cc.blynk.common.model.messages.protocol;

import cc.blynk.common.model.messages.Message;

import static cc.blynk.common.enums.Command.REGISTER;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class RegisterMessage extends Message {

    public RegisterMessage(int messageId, String body) {
        super(messageId, REGISTER, body.length(), body);
    }

    @Override
    public String toString() {
        return "RegisterMessage{" + super.toString() + "}";
    }
}
