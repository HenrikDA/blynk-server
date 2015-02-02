package cc.blynk.server.handlers;

import cc.blynk.common.model.messages.protocol.LoadProfileMessage;
import cc.blynk.server.auth.User;
import cc.blynk.server.group.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class LoadProfileHandler extends SimpleChannelInboundHandler<LoadProfileMessage> {

    private static final Logger log = LogManager.getLogger(LoadProfileHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoadProfileMessage message) throws Exception {
        User authUser = Session.findUserByChannel(ctx.channel());

        String body = authUser.getUserProfile() == null ? "{}" : authUser.getUserProfile().toString();
        ctx.writeAndFlush(produce(message.id, message.command, body));
    }

}
