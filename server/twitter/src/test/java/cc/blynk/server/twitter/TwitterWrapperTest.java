package cc.blynk.server.twitter;

import cc.blynk.server.exceptions.TweetException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/7/2015.
 */
public class TwitterWrapperTest {

    @Test
    @Ignore
    public void testTweet() {
        String token = "PUT_YOUR_TOKEN_HERE";
        String tokenSecret = "PUT_YOUR_TOKEN_SECRET_HERE";
        new TwitterWrapper().tweet(token, tokenSecret, "Hello444", 1);
    }

    @Test(expected = TweetException.class)
    public void expectException() {
        new TwitterWrapper().tweet("", "", "Hello 123", 1);
    }

}
