package cc.blynk.server.handlers;

import cc.blynk.common.model.messages.protocol.TweetMessage;
import cc.blynk.server.auth.User;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.auth.session.SessionsHolder;
import cc.blynk.server.exceptions.TweetBodyInvalidException;
import cc.blynk.server.twitter.TwitterWrapper;
import cc.blynk.server.utils.FileManager;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 *
 */
public class TweetHandler extends BaseSimpleChannelInboundHandler<TweetMessage> {

    private static final Logger log = LogManager.getLogger(TweetHandler.class);

    private final TwitterWrapper twitterWrapper;

    public TweetHandler(FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder, TwitterWrapper twitterWrapper) {
        super(fileManager, userRegistry, sessionsHolder);
        this.twitterWrapper = twitterWrapper;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TweetMessage message) throws Exception {
        if (message.body == null || message.body.equals("") || message.body.length() > 140) {
            throw new TweetBodyInvalidException("Tweet message is empty or larger 140 chars", message.id);
        }
        User user = sessionsHolder.findUserByChannel(ctx.channel(), message.id);
        twitterWrapper.tweet(user.getUserProfile().getTwitterAccessToken(), message.body, message.id);
        log.debug("Tweet for user {}, with message : '{}', successfully was sent.", user.getName(), message.body);

        ctx.channel().writeAndFlush(produce(message.id, OK));
    }


}
