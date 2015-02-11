package cc.blynk.server.handlers.workflow;

import cc.blynk.common.model.messages.protocol.HardwareMessage;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.model.auth.User;
import cc.blynk.server.model.auth.session.Session;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

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

    public HardwareHandler(FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        super(fileManager, userRegistry, sessionsHolder);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, User user, HardwareMessage message) throws Exception {
        Session group = sessionsHolder.getUserSession().get(user);

        //todo
        //for hardware command do not wait for hardware response.
        List<ChannelFuture> futures = group.sendMessageToHardware(message);
        ctx.channel().writeAndFlush(produce(message.id, OK));

    }


}
