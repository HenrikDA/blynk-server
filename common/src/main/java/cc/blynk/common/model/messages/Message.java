package cc.blynk.common.model.messages;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 * Yes, I don't use getters and setters, inlining is not always works as expected.
 *
 * IMPORTANT : have in mind, in body we retrieve always unsigned bytes, shorts, while in java
 * is only signed types, so we require 2 times larger types.
 */
public abstract class Message extends MessageBase {

    public String body;

    public Message(int messageId, short command, int length, String body) {
        super(messageId, command, length);
        this.body = body;
    }

    @Override
    public int getByteLength() {
        return super.getByteLength() + length;
    }

    @Override
    public String toString() {
        return super.toString() + ", body='" + body + "'";
    }
}
