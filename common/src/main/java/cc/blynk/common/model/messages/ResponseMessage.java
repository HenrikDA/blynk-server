package cc.blynk.common.model.messages;

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
                ", command=" + command +
                ", responseCode=" + length + "}";
    }
}
