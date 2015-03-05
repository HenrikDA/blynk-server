package cc.blynk.client;

import cc.blynk.common.model.messages.Message;
import cc.blynk.common.model.messages.protocol.LoginMessage;
import cc.blynk.common.utils.Config;

import java.nio.ByteBuffer;

public class HexConvertor {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static void main(String args[]) {
        Message message = new LoginMessage(1, "username@example.com UserPassword");
        System.out.println(messageToHex(message));
    }


    public static String messageToHex(Message message) {
        ByteBuffer bb = ByteBuffer.allocate(message.getByteLength());
        bb.put((byte) message.command);
        bb.putShort((short) message.id);
        bb.putShort((short) message.length);

        if (message.length > 0) {
            bb.put(message.body.getBytes(Config.DEFAULT_CHARSET));
        }

        return bytesToHex(bb.array());
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


}
