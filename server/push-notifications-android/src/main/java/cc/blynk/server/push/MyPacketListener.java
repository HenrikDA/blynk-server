package cc.blynk.server.push;

import cc.blynk.server.push.response.ACKMessage;
import cc.blynk.server.push.response.ControlMessage;
import cc.blynk.server.push.response.NACKMessage;
import cc.blynk.server.push.response.ResponseMessageBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import static cc.blynk.server.push.GCMPacketExtension.GCM_NAMESPACE;

/**
* The Blynk Project.
* Created by Dmitriy Dumanskiy.
* Created on 2/8/2015.
*/
class MyPacketListener implements PacketListener {

    private static final Logger log = LogManager.getLogger(MyPacketListener.class);

    private GCMSmackCcsClient gcmSmackCcsClient;

    public MyPacketListener(GCMSmackCcsClient gcmSmackCcsClient) {
        this.gcmSmackCcsClient = gcmSmackCcsClient;
    }

    @Override
    public void processPacket(Packet packet) {
        log.debug("Incomming GCM respones : {}", packet.toXML());
        Message incomingMessage = (Message) packet;
        GCMPacketExtension gcmPacket = (GCMPacketExtension) incomingMessage.getExtension(GCM_NAMESPACE);
        String json = gcmPacket.getJson();
        try {
            ResponseMessageBase responseMessageBase = GCMSmackCcsClient.mapper.readValue(json, ResponseMessageBase.class);
            log.debug("Incomming GCM respones : {}", responseMessageBase);
            // present for "ack"/"nack", null otherwise

            if (responseMessageBase instanceof ACKMessage) {
                gcmSmackCcsClient.handleAckReceipt((ACKMessage) responseMessageBase);
            } else if (responseMessageBase instanceof NACKMessage) {
                gcmSmackCcsClient.handleNackReceipt((NACKMessage) responseMessageBase);
            } else if (responseMessageBase instanceof ControlMessage) {
                gcmSmackCcsClient.handleControlMessage((ControlMessage) responseMessageBase);
            } else {
                log.error("Not implemented!!!");
                /*
                // Normal upstream data message
                handleUpstreamMessage(jsonObject);

                // Send ACK to CCS
                String messageId = (String) jsonObject.get("message_id");
                String from = (String) jsonObject.get("from");
                String ack = createJsonAck(from, messageId);
                send(ack);
                */
            }

        } catch (Exception e) {
            log.error("Failed to process packet", e);
        }
    }
}
