package cc.blynk.common.handlers.encoders;

import cc.blynk.common.model.messages.Message;
import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.common.utils.Config;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class DeviceMessageEncoder extends MessageToByteEncoder<MessageBase> {

    protected static final Logger log = LogManager.getLogger(DeviceMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageBase message, ByteBuf out) throws Exception {
        out.writeByte(message.command);
        out.writeShort(message.id);
        out.writeShort(message.length);

        if (message.length > 0 && message instanceof Message) {
            out.writeBytes(((Message) message).body.getBytes(Config.DEFAULT_CHARSET));
        }

        log.trace("Out {}", message);
    }
}
