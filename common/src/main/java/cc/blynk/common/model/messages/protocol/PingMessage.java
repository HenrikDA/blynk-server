package cc.blynk.common.model.messages.protocol;

import cc.blynk.common.model.messages.Message;

import static cc.blynk.common.enums.Command.PING;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class PingMessage extends Message {

    public PingMessage(int messageId, String body) {
        super(messageId, PING, body.length(), body);
    }

    @Override
    public String toString() {
        return "PingMessage{" + super.toString() + "}";
    }
}
