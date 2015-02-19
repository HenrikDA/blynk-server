package cc.blynk.server.handlers.workflow;

import cc.blynk.common.model.messages.protocol.GetTokenMessage;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.exceptions.IllegalCommandException;
import cc.blynk.server.model.auth.User;
import io.netty.channel.ChannelHandlerContext;

import java.util.Properties;

import static cc.blynk.common.model.messages.MessageFactory.produce;
import static cc.blynk.common.utils.PropertiesUtil.getIntProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class GetTokenHandler extends BaseSimpleChannelInboundHandler<GetTokenMessage> {

    private final int MIN_DASH_ID;
    private final int MAX_DASH_ID;

    public GetTokenHandler(Properties properties, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        super(properties, fileManager, userRegistry, sessionsHolder);
        this.MIN_DASH_ID = getIntProperty(properties, "user.dashboard.id.min");
        this.MAX_DASH_ID = getIntProperty(properties, "user.dashboard.id.max");
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, User user, GetTokenMessage message) throws Exception {
        String dashBoardIdString = message.body;

        int dashBoardId;
        try {
            dashBoardId = Integer.parseInt(dashBoardIdString);
        } catch (NumberFormatException ex) {
            throw new IllegalCommandException(String.format("Dash board id '%s' not valid.", dashBoardIdString), message.id);
        }

        if (dashBoardId < MIN_DASH_ID || dashBoardId > MAX_DASH_ID) {
            throw new IllegalCommandException(
                    String.format("Token '%s' should be in range [%d..%d].", dashBoardIdString, MIN_DASH_ID, MAX_DASH_ID),
                    message.id);
        }

        String token = userRegistry.getToken(user, dashBoardId);

        ctx.writeAndFlush(produce(message.id, message.command, token));
    }
}
