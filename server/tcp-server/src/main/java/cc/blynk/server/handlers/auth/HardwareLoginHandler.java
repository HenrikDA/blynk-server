package cc.blynk.server.handlers.auth;

import cc.blynk.common.handlers.DefaultExceptionHandler;
import cc.blynk.common.model.messages.protocol.appllication.LoginMessage;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.exceptions.IllegalCommandException;
import cc.blynk.server.exceptions.InvalidTokenException;
import cc.blynk.server.model.auth.User;
import cc.blynk.server.model.auth.nio.ChannelState;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * Handler responsible for managing hardware and apps login messages.
 * Initializes netty channel with a state tied with user.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
@ChannelHandler.Sharable
public class HardwareLoginHandler extends SimpleChannelInboundHandler<LoginMessage> implements DefaultExceptionHandler {

    protected final FileManager fileManager;
    protected final UserRegistry userRegistry;
    protected final SessionsHolder sessionsHolder;

    public HardwareLoginHandler(FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        this.fileManager = fileManager;
        this.userRegistry = userRegistry;
        this.sessionsHolder = sessionsHolder;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMessage message) throws Exception {
        String[] messageParts = message.body.split(" ", 2);

        if (messageParts.length != 1) {
            throw new IllegalCommandException("Wrong income message format.", message.id);
        }

        String token = messageParts[0];
        User user = userRegistry.getUserByToken(token);

        if (user == null) {
            throw new InvalidTokenException(String.format("Hardware token is invalid. Token '%s', %s", token, ctx.channel()), message.id);
        }

        Integer dashId = UserRegistry.getDashIdByToken(user, token);

        ChannelState channelState = (ChannelState) ctx.channel();
        channelState.dashId = dashId;
        channelState.isHardwareChannel = true;
        channelState.user = user;

        sessionsHolder.addChannelToGroup(user, channelState, message.id);

        log.info("Adding hardware channel with id {} to userGroup {}.", ctx.channel(), user.getName());

        ctx.writeAndFlush(produce(message.id, OK));

        //send Pin Mode command in case channel connected to active dashboard with Pin Mode command that
        //was sent previously
        if (dashId.equals(user.getUserProfile().getActiveDashId()) && user.getUserProfile().getPinModeMessage() != null) {
            ctx.writeAndFlush(user.getUserProfile().getPinModeMessage());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       handleGeneralException(ctx, cause);
    }
}
