package cc.blynk.server.handlers;

import cc.blynk.common.model.messages.protocol.HardwareMessage;
import cc.blynk.server.auth.User;
import cc.blynk.server.group.ChannelGroup;
import cc.blynk.server.group.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class HardwareHandler extends SimpleChannelInboundHandler<HardwareMessage> {

    private static final Logger log = LogManager.getLogger(HardwareHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HardwareMessage message) throws Exception {
        //this means not authentificated attempt
        User authUser = Session.findUserByChannel(ctx.channel());

        ChannelGroup group = Session.getBridgeGroup().get(authUser);

        group.sendMessageToHardware(ctx, message);
    }


}
