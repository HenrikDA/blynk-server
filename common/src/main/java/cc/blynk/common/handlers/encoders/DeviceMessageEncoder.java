package cc.blynk.common.handlers.encoders;

import cc.blynk.common.model.messages.Message;
import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.common.utils.Config;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class DeviceMessageEncoder extends MessageToByteEncoder<MessageBase> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageBase messageBase, ByteBuf out) throws Exception {
        out.writeByte(messageBase.command);
        out.writeShort(messageBase.id);
        out.writeShort(messageBase.length);

        if (messageBase.length > 0 && messageBase instanceof Message) {
            out.writeBytes(((Message) messageBase).body.getBytes(Config.DEFAULT_CHARSET));
        }
    }
}
