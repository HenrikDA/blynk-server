package cc.blynk.common.model.messages.protocol.hardware;

import cc.blynk.common.model.messages.Message;

import static cc.blynk.common.enums.Command.TWEET;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class TweetMessage extends Message {

    public TweetMessage(int messageId, String body) {
        super(messageId, TWEET, body.length(), body);
    }

    @Override
    public String toString() {
        return "TweetMessage{" + super.toString() + "}";
    }
}
