package cc.blynk.server.push.request;

import java.util.Map;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/8/2015.
 */
public class RequestMessage {

    public String to;

    public String message_id;

    public Map<String, String> data;

    public long time_to_live;

    public boolean delay_while_idle;

    public boolean delivery_receipt_requested;

    public RequestMessage(String to, String message_id, Map<String, String> data, long time_to_live, boolean delay_while_idle) {
        this.to = to;
        this.message_id = message_id;
        this.data = data;
        this.time_to_live = time_to_live;
        this.delay_while_idle = delay_while_idle;
        this.delivery_receipt_requested = false;
    }
}
