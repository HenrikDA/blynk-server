package cc.blynk.server.utils;

import cc.blynk.server.exceptions.IllegalCommandException;
import cc.blynk.server.model.UserProfile;
import cc.blynk.server.model.auth.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 15:31
 */
public final class JsonParser {

    private static final Logger log = LogManager.getLogger(JsonParser.class);

    //it is threadsafe
    private static ObjectMapper mapper = init();

    public static void check() {

    }

    private static ObjectMapper init() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Error jsoning object.");
            log.error(e);
        }
        return "{}";
    }

    public static User parseUser(String reader) throws IOException {
        User user = mapper.reader(User.class).readValue(reader);
        user.initQuota();
        return user;
    }

    public static UserProfile parseProfile(String reader, int id) {
        try {
            return mapper.reader(UserProfile.class).readValue(reader);
        } catch (IOException e) {
            throw new IllegalCommandException("Error parsing user profile. Reason : " + e.getMessage(), id);
        }
    }

    //only for tests
    public static UserProfile parseProfile(InputStream reader) {
        try {
            return mapper.reader(UserProfile.class).readValue(reader);
        } catch (IOException e) {
            throw new IllegalCommandException("Error parsing user profile. Reason : " + e.getMessage(), 1);
        }
    }

}
