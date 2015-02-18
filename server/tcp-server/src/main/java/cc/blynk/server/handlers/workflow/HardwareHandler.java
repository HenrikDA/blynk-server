package cc.blynk.server.handlers.workflow;

import cc.blynk.common.model.messages.protocol.HardwareMessage;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.model.auth.ChannelState;
import cc.blynk.server.model.auth.Session;
import cc.blynk.server.model.auth.User;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;
import static cc.blynk.common.utils.StringUtils.split;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class HardwareHandler extends BaseSimpleChannelInboundHandler<HardwareMessage> {

    public HardwareHandler(FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        super(fileManager, userRegistry, sessionsHolder);
    }

    private static void storeValue(String body, User user, ChannelState channelState) {
        String[] splitted = split(body, '\0');
        if (splitted[0].charAt(1) == 'w') {
            Byte pin = Byte.valueOf(splitted[1]);
            if (user.getUserProfile().hasGraphPin(channelState.dashId, pin)) {
                //todo store
            }
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, User user, HardwareMessage message) throws Exception {
        Session session = sessionsHolder.getUserSession().get(user);

        ChannelState channelState = (ChannelState) ctx.channel();

        //todo
        //for hardware command do not wait for hardware response.
        List<ChannelFuture> futures;
        if (channelState.isHardwareChannel) {
            //if message from hardware, check if it belongs to graph. so we need save it in that case
            storeValue(message.body, user, channelState);
            futures = Session.sendMessageTo(message, session.getAppChannels());
        } else {
            futures = Session.sendMessageTo(message, session.getHardwareChannels());
        }

        ctx.channel().writeAndFlush(produce(message.id, OK));
    }

}
