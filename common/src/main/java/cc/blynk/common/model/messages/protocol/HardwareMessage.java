package cc.blynk.common.model.messages.protocol;

import cc.blynk.common.model.messages.Message;

import static cc.blynk.common.enums.Command.HARDWARE_COMMAND;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class HardwareMessage extends Message {

    public HardwareMessage(int messageId, String body) {
        super(messageId, HARDWARE_COMMAND, body == null ? 0 : body.length(), body);
    }

    @Override
    public String toString() {
        return "HardwareMessage{" + super.toString() + "}";
    }
}
