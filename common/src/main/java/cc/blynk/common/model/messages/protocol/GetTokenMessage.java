package cc.blynk.common.model.messages.protocol;

import cc.blynk.common.model.messages.Message;

import static cc.blynk.common.enums.Command.GET_TOKEN;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class GetTokenMessage extends Message {

    public GetTokenMessage(int messageId, String body) {
        super(messageId, GET_TOKEN, body.length(), body);
    }

    @Override
    public String toString() {
        return "GetTokenMessage{" + super.toString() + "}";
    }
}
