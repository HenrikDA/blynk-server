package cc.blynk.server.handlers.workflow;

import cc.blynk.common.model.messages.protocol.HardwareMessage;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.dao.*;
import cc.blynk.server.exceptions.DeviceNotInNetworkException;
import cc.blynk.server.exceptions.IllegalCommandException;
import cc.blynk.server.model.auth.Session;
import cc.blynk.server.model.auth.User;
import cc.blynk.server.model.auth.nio.ChannelState;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
@ChannelHandler.Sharable
public class HardwareHandler extends BaseSimpleChannelInboundHandler<HardwareMessage> {

    private final Storage storage;

    public HardwareHandler(ServerProperties props, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        super(props, fileManager, userRegistry, sessionsHolder);
        this.storage = new GraphInMemoryStorage(props.getIntProperty("user.in.memory.storage.limit"));
    }

    private static boolean pinModeMessage(String body) {
        return body != null && body.charAt(0) == 'p';
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, User user, HardwareMessage message) throws Exception {
        Session session = sessionsHolder.getUserSession().get(user);

        ChannelState channelState = (ChannelState) ctx.channel();

        //todo
        //for hardware command do not wait for hardware response.
        if (channelState.isHardwareChannel) {
            //if message from hardware, check if it belongs to graph. so we need save it in that case
            String body = storage.store(user, channelState.dashId, message.body, message.id);
            Session.sendMessageTo(message.updateMessageBody(body), session.appChannels);
        } else {
            if (user.getUserProfile().getActiveDashId() == null) {
                throw new IllegalCommandException("No active dashboard.", message.id);
            }

            if (session.hardwareChannels.size() == 0) {
                if (pinModeMessage(message.body) && user.getUserProfile().isJustActivated()) {
                    log.trace("No device and Pin Mode message catch. Remembering.");
                    user.getUserProfile().setPinModeMessage(message);
                    user.getUserProfile().setJustActivated(false);
                }
                throw new DeviceNotInNetworkException("No device in session.", message.id);
            }

            session.sendMessageToHardware(user.getUserProfile().getActiveDashId(), message);
        }

    }

}
