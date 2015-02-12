package cc.blynk.server;

import cc.blynk.common.utils.PropertiesUtil;

import java.util.Properties;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/12/2015.
 */
public class TestBase {

    public Properties properties = PropertiesUtil.loadProperties("server.properties");
    public String dataFolder = properties.getProperty("data.folder");

}
