package cc.blynk.common.model.messages.protocol.appllication;

import cc.blynk.common.model.messages.Message;

import static cc.blynk.common.enums.Command.SAVE_PROFILE;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class SaveProfileMessage extends Message {

    public SaveProfileMessage(int messageId, String body) {
        super(messageId, SAVE_PROFILE, body.length(), body);
    }

    @Override
    public String toString() {
        return "SaveProfileMessage{" + super.toString() + "}";
    }
}
