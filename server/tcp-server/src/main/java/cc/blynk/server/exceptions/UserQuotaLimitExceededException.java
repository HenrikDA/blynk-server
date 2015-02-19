package cc.blynk.server.exceptions;

import cc.blynk.common.enums.Response;
import cc.blynk.common.exceptions.BaseServerException;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public class UserQuotaLimitExceededException extends BaseServerException {

    public UserQuotaLimitExceededException(String message, int msgId) {
        super(message, msgId, Response.DEVICE_NOT_IN_NETWORK);
    }

}
