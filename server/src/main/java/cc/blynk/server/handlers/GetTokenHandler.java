package cc.blynk.server.handlers;

import cc.blynk.common.model.messages.protocol.GetTokenMessage;
import cc.blynk.server.auth.User;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.group.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.common.enums.Response.INVALID_COMMAND_FORMAT;
import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class GetTokenHandler extends SimpleChannelInboundHandler<GetTokenMessage> {

    private static final Logger log = LogManager.getLogger(GetTokenHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GetTokenMessage message) throws Exception {
        String dashBoardIdString = message.body;

        Long dashBoardId;
        try {
            dashBoardId = Long.parseLong(dashBoardIdString);
        } catch (NumberFormatException ex) {
            log.error("Dash board id {} not valid.", dashBoardIdString);
            ctx.writeAndFlush(produce(message.id, INVALID_COMMAND_FORMAT));
            return;
        }

        User user = Session.findUserByChannel(ctx.channel());
        String token = UserRegistry.getToken(user, dashBoardId);

        ctx.writeAndFlush(produce(message.id, message.command, token));
    }


}