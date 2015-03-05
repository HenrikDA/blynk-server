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
import io.netty.channel.ChannelHandler;
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
@ChannelHandler.Sharable
public class SaveProfileHandler extends BaseSimpleChannelInboundHandler<SaveProfileMessage> {

    //I have to use volatile for reloadable props to be sure updated value will be visible by all threads
    private volatile int DASH_MAX_LIMIT;

    //I have to use volatile for reloadable props to be sure updated value will be visible by all threads
    private volatile int USER_PROFILE_MAX_SIZE;

    public SaveProfileHandler(Properties props, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        super(props, fileManager, userRegistry, sessionsHolder);
        updateProperties(props);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, User user, SaveProfileMessage message) throws Exception {
        String userProfileString = message.body;

        //expecting message with 2 parts
        if (userProfileString == null || userProfileString.equals("")) {
            throw new IllegalCommandException("Save Profile Handler. Income profile message is empty.", message.id);
        }

        if (userProfileString.length() > USER_PROFILE_MAX_SIZE) {
            throw new NotAllowedException(String.format("User profile size is larger than %d bytes.", USER_PROFILE_MAX_SIZE), message.id);
        }

        log.debug("Trying to parse user profile : {}", userProfileString);
        UserProfile userProfile = JsonParser.parseProfile(userProfileString, message.id);

        if (userProfile.getDashBoards() != null && userProfile.getDashBoards().length > DASH_MAX_LIMIT) {
            throw new NotAllowedException(
                    String.format("Not allowed to create more than %s dashboards.", DASH_MAX_LIMIT), message.id);
        }

        log.info("Trying save user profile.");

        userProfile.calcGraphPins();

        user.setUserProfile(userProfile);
        ctx.writeAndFlush(produce(message.id, OK));
    }

    @Override
    public void updateProperties(Properties props) {
        super.updateProperties(props);
        this.DASH_MAX_LIMIT = getIntProperty(props, "user.dashboard.max.limit");
        this.USER_PROFILE_MAX_SIZE = getIntProperty(props, "user.profile.max.size") * 1024;
    }

}
