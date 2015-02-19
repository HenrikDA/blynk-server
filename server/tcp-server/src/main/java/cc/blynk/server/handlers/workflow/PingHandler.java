package cc.blynk.server.handlers.workflow;

import cc.blynk.common.model.messages.protocol.PingMessage;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.model.auth.Session;
import cc.blynk.server.model.auth.User;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Properties;

import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class PingHandler extends BaseSimpleChannelInboundHandler<PingMessage> {

    public PingHandler(Properties properties,FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        super(properties, fileManager, userRegistry, sessionsHolder);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, User user, PingMessage message) throws Exception {
        Session group = sessionsHolder.getUserSession().get(user);
        List<ChannelFuture> futures = group.sendMessageToHardware(message);

        int length = futures.size();
        //todo works for now only for 1 hardware, not for many
        for (ChannelFuture future : futures) {
            future.addListener(future1 -> {
                ctx.channel().writeAndFlush(produce(message.id, OK));
            });
        }
    }



}
