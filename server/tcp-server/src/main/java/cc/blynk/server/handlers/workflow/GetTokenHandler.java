package cc.blynk.server.handlers.workflow;

import cc.blynk.common.model.messages.protocol.GetTokenMessage;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.exceptions.IllegalCommandException;
import cc.blynk.server.model.auth.User;
import cc.blynk.server.utils.FileManager;
import io.netty.channel.ChannelHandlerContext;

import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class GetTokenHandler extends BaseSimpleChannelInboundHandler<GetTokenMessage> {

    public GetTokenHandler(FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        super(fileManager, userRegistry, sessionsHolder);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, User user, GetTokenMessage message) throws Exception {
        String dashBoardIdString = message.body;

        long dashBoardId;
        try {
            dashBoardId = Long.parseLong(dashBoardIdString);
        } catch (NumberFormatException ex) {
            throw new IllegalCommandException(String.format("Dash board id '%s' not valid.", dashBoardIdString), message.id);
        }

        if (dashBoardId < 0 || dashBoardId > 100) {
            throw new IllegalCommandException(String.format("Token '%s' should ne in range [0..100].", dashBoardIdString), message.id);
        }

        String token = userRegistry.getToken(user, dashBoardId);

        ctx.writeAndFlush(produce(message.id, message.command, token));
    }
}
