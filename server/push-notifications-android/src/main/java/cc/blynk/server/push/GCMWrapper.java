package cc.blynk.server.push;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static cc.blynk.server.push.GCMSmackCcsClient.createRequest;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/8/2015.
 */
public class GCMWrapper {

    private static final Logger log = LogManager.getLogger(GCMSmackCcsClient.class);

    private static final String filePropertiesName = "/gcm.properties";
    private final Properties props = new Properties();
    private GCMSmackCcsClient ccsClient;

    public GCMWrapper() {
        try (InputStream classPath = GCMWrapper.class.getResourceAsStream(filePropertiesName);
             InputStream curFolder = GCMWrapper.class.getResourceAsStream("." + filePropertiesName)) {

            if (classPath != null) {
                props.load(classPath);
            }
            if (curFolder != null) {
                props.load(curFolder);
            }
            this.ccsClient = new GCMSmackCcsClient(props.getProperty("gcm.server"), Integer.valueOf(props.getProperty("gcm.port")));
            ccsClient.connect(Long.valueOf(props.getProperty("gcm.project.id")), props.getProperty("gcm.api.key"));
        } catch (Exception e) {
            log.error("Error connecting to google push server.", e);
        }
    }

    public void sendMessage(String toRegId, Map<String, String> payload) throws Exception {
        final String messageId = GCMSmackCcsClient.generateUniqueMesageId();
        final long timeToLive = 86400;

        String message = createRequest(toRegId, messageId, payload, timeToLive, true);
        ccsClient.sendDownstreamMessage(message);
    }

}
