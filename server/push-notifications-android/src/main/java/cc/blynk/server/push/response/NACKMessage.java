package cc.blynk.server.push.response;

import cc.blynk.server.push.response.enums.NACKError;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/8/2015.
 */
public class NACKMessage extends ResponseMessageBase {

    public NACKError error;

    public String error_description;


    @Override
    public String toString() {
        return "NACKMessage{" +
                "error='" + error + '\'' +
                ", error_description='" + error_description + "'," +
                 super.toString() + "}";
    }
}
