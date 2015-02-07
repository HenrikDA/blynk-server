package cc.blynk.server.exceptions;

import cc.blynk.common.enums.Response;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public class TweetBodyInvalidException extends BaseServerException {

    public TweetBodyInvalidException(String message, int msgId) {
        super(message, msgId, Response.TWEET_BODY_INVALID_EXCEPTION);
    }

}
