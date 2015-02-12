package cc.blynk.server.push;

import cc.blynk.common.utils.PropertiesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final String filePropertiesName = "gcm.properties";
    private final Properties props;
    private GCMSmackCcsClient ccsClient;

    public GCMWrapper() {
        props = PropertiesUtil.loadProperties(filePropertiesName);

        this.ccsClient = new GCMSmackCcsClient(props.getProperty("gcm.server"), PropertiesUtil.getIntProperty(props, "gcm.port"));

        try {
            ccsClient.connect(PropertiesUtil.getLongProperty(props, "gcm.project.id"), props.getProperty("gcm.api.key"));
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
