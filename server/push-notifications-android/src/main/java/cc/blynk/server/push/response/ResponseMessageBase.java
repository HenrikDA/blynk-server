package cc.blynk.server.push.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/8/2015.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "message_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ACKMessage.class, name = "ack"),
        @JsonSubTypes.Type(value = NACKMessage.class, name = "nack"),
        @JsonSubTypes.Type(value = NACKMessage.class, name = "control")
})
public abstract class ResponseMessageBase {

    public String from;

    public String message_id;

    //public String message_type;


    @Override
    public String toString() {
        return "from='" + from + '\'' +
                ", message_id='" + message_id + '\'';
    }
}
