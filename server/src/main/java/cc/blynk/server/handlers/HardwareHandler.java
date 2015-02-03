package cc.blynk.server.handlers;

import cc.blynk.common.model.messages.protocol.HardwareMessage;
import cc.blynk.server.auth.User;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.group.ChannelGroup;
import cc.blynk.server.group.Session;
import cc.blynk.server.utils.FileManager;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class HardwareHandler extends BaseSimpleChannelInboundHandler<HardwareMessage> {

    private static final Logger log = LogManager.getLogger(HardwareHandler.class);

    public HardwareHandler(FileManager fileManager, UserRegistry userRegistry) {
        super(fileManager, userRegistry);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HardwareMessage message) throws Exception {
        //this means not authentificated attempt
        User authUser = Session.findUserByChannel(ctx.channel());

        ChannelGroup group = Session.getBridgeGroup().get(authUser);

        group.sendMessageToHardware(ctx, message);
    }


}
