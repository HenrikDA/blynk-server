package cc.blynk.common.exceptions;

import cc.blynk.common.enums.Response;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public class UnsupportedCommandException extends BaseServerException {

    public UnsupportedCommandException(String message, int msgId) {
        super(message, msgId, Response.ILLEGAL_COMMAND);
    }

}
