package cc.blynk.server.handlers;

import cc.blynk.common.model.messages.protocol.LoginMessage;
import cc.blynk.server.auth.User;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.group.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.common.enums.Response.*;
import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class LoginHandler extends SimpleChannelInboundHandler<LoginMessage> {

    private static final Logger log = LogManager.getLogger(LoginHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMessage message) throws Exception {
        String[] messageParts = message.body.split(" ", 2);

        if (messageParts.length == 0) {
            log.error("Wrong income message format.");
            ctx.writeAndFlush(produce(message.id, INVALID_COMMAND_FORMAT));
            return;
        }

        if (messageParts.length == 1) {
            hardwareLogin(ctx, message.id, messageParts[0]);
        } else if (messageParts.length == 2) {
            appLogin(ctx, message.id, messageParts[0], messageParts[1]);
        }


        ctx.writeAndFlush(produce(message.id, OK));
    }

    ///todo optimize/simplify
    private void hardwareLogin(ChannelHandlerContext ctx, int messageId, String token) {
        User user = UserRegistry.getByToken(token);

        if (user == null) {
            ctx.writeAndFlush(produce(messageId, INVALID_TOKEN));
            throw new IllegalArgumentException("Hardware token is invalid. Token '" + token + "', " + ctx.channel());
        }

        Session.addHardwareChannelToGroup(user, ctx.channel());

        log.info("Adding hardware channel with id {} to userGroup {}.", ctx.channel(), user.getName());
    }

    private void appLogin(ChannelHandlerContext ctx, int messageId, String username, String pass) {
        String userName = username.toLowerCase();
        User user = UserRegistry.getByName(userName);

        //todo fix pass validation
        if (user == null || !user.getPass().equals(pass)) {
            ctx.writeAndFlush(produce(messageId, USER_NOT_AUTHENTICATED));
            throw new IllegalArgumentException("User credentials are wrong. Username '" + userName + "', " + ctx.channel());
        }

        Session.addAppChannelToGroup(user, ctx.channel());

        log.info("Adding app channel with id {} to userGroup {}.", ctx.channel(), user.getName());
    }

}
