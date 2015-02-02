package cc.blynk.common.handlers.encoders;

import cc.blynk.common.model.messages.Message;
import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.common.utils.Config;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class DeviceMessageEncoder extends MessageToByteEncoder<MessageBase> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageBase messageBase, ByteBuf out) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(messageBase.getByteLength());
        byteBuffer.put((byte) messageBase.command);
        byteBuffer.putShort((short) messageBase.id);
        byteBuffer.putShort((short) messageBase.length);

        if (messageBase instanceof Message) {
            Message message = (Message) messageBase;
            if (message.length > 0) {
                byteBuffer.put(message.body.getBytes(Config.DEFAULT_CHARSET));
            }
        }

        out.writeBytes(byteBuffer.array());
    }
}
