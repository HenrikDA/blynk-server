package cc.blynk.server.handlers.workflow;

import cc.blynk.common.model.messages.protocol.PingMessage;
import cc.blynk.server.auth.User;
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
public class PingHandler extends BaseSimpleChannelInboundHandler<PingMessage> {

    private static final Logger log = LogManager.getLogger(PingHandler.class);

    public PingHandler(FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        super(fileManager, userRegistry, sessionsHolder);
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
