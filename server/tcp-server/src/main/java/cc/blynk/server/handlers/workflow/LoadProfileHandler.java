package cc.blynk.server.handlers.workflow;

import cc.blynk.common.model.messages.protocol.LoadProfileMessage;
import cc.blynk.server.auth.User;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.auth.session.SessionsHolder;
import cc.blynk.server.utils.FileManager;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class LoadProfileHandler extends BaseSimpleChannelInboundHandler<LoadProfileMessage> {

    public LoadProfileHandler(FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        super(fileManager, userRegistry, sessionsHolder);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, User user, LoadProfileMessage message) throws Exception {
        String body = user.getUserProfile() == null ? "{}" : user.getUserProfile().toString();
        ctx.writeAndFlush(produce(message.id, message.command, body));
    }

}
