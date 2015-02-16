package cc.blynk.client.enums;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/16/2015.
 */
public enum ClientMode {

    APP, HARDWARE;

    public static ClientMode parse(String val) {
        for (ClientMode clientMode : values()) {
            if (clientMode.name().equalsIgnoreCase(val)) {
                return clientMode;
            }
        }

        throw new RuntimeException("Wrong client mode. app and hardware only supported.");
    }

}
