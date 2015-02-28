package cc.blynk.server.handlers.workflow;

import cc.blynk.server.dao.SessionsHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/20/2015.
 *
 * Removes channel from session in case it became inactive (closed from client side).
 */
@ChannelHandler.Sharable
public class ClientChannelStateHandler extends ChannelDuplexHandler {

    private final SessionsHolder sessionsHolder;

    public ClientChannelStateHandler(SessionsHolder sessionsHolder) {
        this.sessionsHolder = sessionsHolder;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        sessionsHolder.removeFromSession(ctx.channel());
        super.channelInactive(ctx);
    }
}
