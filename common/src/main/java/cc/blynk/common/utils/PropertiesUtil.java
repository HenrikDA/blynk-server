package cc.blynk.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/12/2015.
 */
public class PropertiesUtil {

    private static final Logger log = LogManager.getLogger(PropertiesUtil.class);

    /**
     * First loads properties file from class path after that from current folder.
     * So properties file in current folder is always overrides properties in classpath.
     *
     * @param filePropertiesName - name of properties file, for example "twitter4j.properties"
     * @return - loaded properties
     */
    public static Properties loadProperties(String filePropertiesName) {
        if (!filePropertiesName.startsWith("/")) {
            filePropertiesName = "/" + filePropertiesName;
        }
        Properties props = new Properties();
        try (InputStream classPath = PropertiesUtil.class.getResourceAsStream(filePropertiesName)) {
            if (classPath != null) {
                props.load(classPath);
            }
        } catch (Exception e) {
            log.error("Error getting '{}' properties file.", filePropertiesName, e);
        }

        Path curDirPath = Paths.get(System.getProperty("user.dir"), filePropertiesName);
        if (Files.exists(curDirPath)) {
            try (InputStream curFolder = Files.newInputStream(curDirPath)) {
                if (curFolder != null) {
                    props.load(curFolder);
                }
            } catch (Exception e) {
                log.error("Error getting '{}' properties file.", filePropertiesName, e);
            }
        }

        return props;
    }

    public static int getIntProperty(Properties props, String propertyName) {
        return ParseUtil.parseInt(props.getProperty(propertyName));
    }

    public static boolean getBoolProperty(Properties props, String propertyName) {
        return Boolean.parseBoolean(props.getProperty(propertyName));
    }

    public static long getLongProperty(Properties props, String propertyName) {
        return ParseUtil.parseLong(props.getProperty(propertyName));
    }

    public static File getFileFromResources(String path) throws URISyntaxException {
        return new File(PropertiesUtil.class.getResource(path).toURI());
    }

}
