package cc.blynk.server.handlers.workflow;

import cc.blynk.common.model.messages.protocol.LoadProfileMessage;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.model.auth.User;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
@ChannelHandler.Sharable
public class LoadProfileHandler extends BaseSimpleChannelInboundHandler<LoadProfileMessage> {

    public LoadProfileHandler(ServerProperties props, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        super(props, fileManager, userRegistry, sessionsHolder);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, User user, LoadProfileMessage message) throws Exception {
        String body = user.getUserProfile().toString();
        ctx.writeAndFlush(produce(message.id, message.command, body));
    }

}
