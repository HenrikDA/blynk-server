package cc.blynk.server;

import cc.blynk.common.utils.ServerProperties;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/12/2015.
 */
public class TestBase {

    public ServerProperties props = new ServerProperties("server.properties");
    public String dataFolder = props.getProperty("data.folder");

}
