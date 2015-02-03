package cc.blynk.common.model.messages;

import cc.blynk.common.enums.Command;
import cc.blynk.common.enums.Response;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class ResponseMessage extends MessageBase {

    public ResponseMessage(int messageId, short command, int length) {
        super(messageId, command, length);
    }

    @Override
    public String toString() {
        return "ResponseMessage{id=" + id +
                ", command=" + Command.getNameByValue(command) +
                ", responseCode=" + Response.getNameByValue(length) + "}";
    }
}
