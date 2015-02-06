package cc.blynk.server.handlers;

import cc.blynk.common.model.messages.protocol.HardwareMessage;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.auth.session.Session;
import cc.blynk.server.auth.session.SessionsHolder;
import cc.blynk.server.utils.FileManager;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class HardwareHandler extends BaseSimpleChannelInboundHandler<HardwareMessage> {

    private static final Logger log = LogManager.getLogger(HardwareHandler.class);

    public HardwareHandler(FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        super(fileManager, userRegistry, sessionsHolder);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HardwareMessage message) throws Exception {
        Session group = sessionsHolder.getUserSession(ctx.channel(), message.id);

        //todo
        //for hardware command do not wait for hardware response.
        List<ChannelFuture> futures = group.sendMessageToHardware(message);
        ctx.channel().writeAndFlush(produce(message.id, OK));

    }


}
