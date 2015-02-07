package cc.blynk.server.exceptions;

import cc.blynk.common.enums.Response;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public class TweetException extends BaseServerException {

    public TweetException(String message, int msgId) {
        super(message, msgId, Response.TWEET_EXCEPTION);
    }

}
