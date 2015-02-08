package cc.blynk.server.auth;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/7/2015.
 */
public class TwitterAccessToken {

    private String token;
    private String tokenSecret;

    public TwitterAccessToken() {
    }

    public TwitterAccessToken(String token, String tokenSecret) {
        this.token = token;
        this.tokenSecret = tokenSecret;
    }

    public String getToken() {
        return token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }
}
