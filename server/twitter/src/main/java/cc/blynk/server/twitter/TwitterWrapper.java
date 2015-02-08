package cc.blynk.server.twitter;

import cc.blynk.server.exceptions.TweetException;
import cc.blynk.server.exceptions.TweetNotAuthorizedException;
import cc.blynk.server.model.TwitterAccessToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/6/2015.
 */
public class TwitterWrapper {

    private static final Logger log = LogManager.getLogger(TwitterWrapper.class);

    // The factory instance is re-useable and thread safe.
    private final TwitterFactory factory = new TwitterFactory();

    public void tweet(TwitterAccessToken twitterAccessToken, String message, int msgId) {
        if (twitterAccessToken == null ||
                twitterAccessToken.getToken() == null || twitterAccessToken.getToken().equals("") ||
                twitterAccessToken.getTokenSecret() == null || twitterAccessToken.getTokenSecret().equals("")) {
            throw new TweetNotAuthorizedException("User has no access token provided.", msgId);
        }
        tweet(twitterAccessToken.getToken(), twitterAccessToken.getTokenSecret(), message, msgId);
    }

    public void tweet(String token, String tokenSecret, String message, int msgId) {
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        Twitter twitter = factory.getInstance();
        twitter.setOAuthAccessToken(accessToken);
        try {
            twitter.updateStatus(message);
        } catch (TwitterException e) {
            throw new TweetException(e.getMessage(), msgId);
        }
    }

}
