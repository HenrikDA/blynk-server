package cc.blynk.server.handlers.workflow;

import cc.blynk.common.model.messages.protocol.SaveProfileMessage;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.exceptions.IllegalCommandException;
import cc.blynk.server.exceptions.NotAllowedException;
import cc.blynk.server.model.UserProfile;
import cc.blynk.server.model.auth.User;
import cc.blynk.server.utils.JsonParser;
import io.netty.channel.ChannelHandlerContext;

import java.util.Properties;

import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;
import static cc.blynk.common.utils.PropertiesUtil.getIntProperty;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class SaveProfileHandler extends BaseSimpleChannelInboundHandler<SaveProfileMessage> {

    private final int MAX_DASH_NUMBER;

    public SaveProfileHandler(Properties properties,FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        super(properties, fileManager, userRegistry, sessionsHolder);
        this.MAX_DASH_NUMBER = getIntProperty(properties, "user.dashboard.max.limit");
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, User user, SaveProfileMessage message) throws Exception {
        String userProfileString = message.body;

        //expecting message with 2 parts
        if (userProfileString == null || userProfileString.equals("")) {
            throw new IllegalCommandException("Save Profile Handler. Income profile message is empty.", message.id);
        }

        log.info("Trying to parseProfile user profile : {}", userProfileString);
        UserProfile userProfile = JsonParser.parseProfile(userProfileString);
        if (userProfile == null) {
            throw new IllegalCommandException("Register Handler. Wrong user profile message format.", message.id);
        }

        if (userProfile.getDashBoards() != null && userProfile.getDashBoards().length > MAX_DASH_NUMBER) {
            throw new NotAllowedException(
                    String.format("Not allowed to create more than %s dashboards.", MAX_DASH_NUMBER), message.id);
        }

        log.info("Trying save user profile.");

        userProfile.calcGraphPins();

        user.setUserProfile(userProfile);
        ctx.writeAndFlush(produce(message.id, OK));
    }


}
