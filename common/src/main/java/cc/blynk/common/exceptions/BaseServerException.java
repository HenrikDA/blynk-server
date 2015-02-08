package cc.blynk.common.exceptions;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public class BaseServerException extends RuntimeException {

    public final int msgId;
    public final int errorCode;

    public BaseServerException(String message, int msgId, int errorCode) {
        super(message);
        this.msgId = msgId;
        this.errorCode = errorCode;
    }

}
