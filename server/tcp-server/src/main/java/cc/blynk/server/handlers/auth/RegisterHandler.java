package cc.blynk.server.handlers.auth;

import cc.blynk.common.model.messages.protocol.RegisterMessage;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.DefaultExceptionHandler;
import cc.blynk.server.utils.EMailValidator;
import cc.blynk.server.utils.FileManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static cc.blynk.common.enums.Response.*;
import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 * Process register message.
 * Divides input sting by spaces on 2 parts:
 * "username" "password".
 * Checks if user not registered yet. If not - registering.
 *
 * For instance, incoming register message may be : "user@mail.ua my_password"
 */
public class RegisterHandler extends SimpleChannelInboundHandler<RegisterMessage> implements DefaultExceptionHandler {

    protected final FileManager fileManager;
    protected final UserRegistry userRegistry;
    protected final SessionsHolder sessionsHolder;

    public RegisterHandler(FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        this.fileManager = fileManager;
        this.userRegistry = userRegistry;
        this.sessionsHolder = sessionsHolder;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterMessage message) throws Exception {
        String[] messageParts = message.body.split(" ", 2);

        //expecting message with 2 parts, described above in comment.
        if (messageParts.length != 2) {
            log.error("Register Handler. Wrong income message format. {}", message);
            ctx.writeAndFlush(produce(message.id, ILLEGAL_COMMAND));
            return;
        }

        String userName = messageParts[0].toLowerCase();
        //TODO encryption, SSL sockets.
        String pass = messageParts[1];
        log.info("Trying register user : {}", userName);

        if (!EMailValidator.isValid(userName)) {
            log.error("Register Handler. Wrong email: {}", userName);
            ctx.writeAndFlush(produce(message.id, ILLEGAL_COMMAND));
            return;
        }

        if (userRegistry.isUserExists(userName)) {
            log.warn("User with name {} already exists.", userName);
            ctx.writeAndFlush(produce(message.id, USER_ALREADY_REGISTERED));
            return;
        }

        userRegistry.createNewUser(userName, pass);

        log.info("Registered {}.", userName);

        ctx.writeAndFlush(produce(message.id, OK));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handleException(ctx, cause);
    }

}
