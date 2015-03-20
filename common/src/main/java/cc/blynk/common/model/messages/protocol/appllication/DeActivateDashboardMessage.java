package cc.blynk.common.model.messages.protocol.appllication;

import cc.blynk.common.model.messages.Message;

import static cc.blynk.common.enums.Command.DEACTIVATE_DASHBOARD;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class DeActivateDashboardMessage extends Message {

    public DeActivateDashboardMessage(int messageId, String body) {
        super(messageId, DEACTIVATE_DASHBOARD, body.length(), body);
    }

    @Override
    public String toString() {
        return "DeActivateDashboardMessage{" + super.toString() + "}";
    }
}
