package cc.blynk.server.handlers;

import cc.blynk.common.model.messages.protocol.RegisterMessage;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.utils.EMailValidator;
import cc.blynk.server.utils.FileManager;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class RegisterHandler extends BaseSimpleChannelInboundHandler<RegisterMessage> {

    private static final Logger log = LogManager.getLogger(RegisterHandler.class);

    public RegisterHandler(FileManager fileManager, UserRegistry userRegistry) {
        super(fileManager, userRegistry);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterMessage message) throws Exception {
        String[] messageParts = message.body.split(" ", 2);

        //expecting message with 2 parts, described above in comment.
        if (messageParts.length != 2) {
            log.error("Register Handler. Wrong income message format. {}", message);
            ctx.writeAndFlush(produce(message.id, INVALID_COMMAND_FORMAT));
            return;
        }

        String userName = messageParts[0].toLowerCase();
        //TODO encryption, SSL sockets.
        String pass = messageParts[1];
        log.info("Trying register user : {}", userName);

        if (!EMailValidator.isValid(userName)) {
            log.error("Register Handler. Wrong email: {}", userName);
            ctx.writeAndFlush(produce(message.id, INVALID_COMMAND_FORMAT));
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

}
