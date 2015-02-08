package cc.blynk.server.push.response.enums;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/8/2015.
 */
public enum NACKError {

    BAD_ACK("The ACK message is improperly formed."),
    BAD_REGISTRATION("The device has a registration ID, but it's invalid or expired."),
    CONNECTION_DRAINING("The message couldn't be processed because the connection is draining. "),
    DEVICE_UNREGISTERED("The device is not registered."),
    INTERNAL_SERVER_ERROR("The server encountered an error while trying to process the request."),
    INVALID_JSON("The JSON message payload is not valid."),
    DEVICE_MESSAGE_RATE_EXCEEDED("The rate of messages to a particular device is too high."),
    SERVICE_UNAVAILABLE("CCS is not currently able to process the message.");

    private String message;

    private NACKError(String message) {
        this.message = message;
    }
}
