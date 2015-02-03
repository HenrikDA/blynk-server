package cc.blynk.server.utils;

import cc.blynk.server.auth.User;
import cc.blynk.server.model.UserProfile;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private static ObjectMapper mapper;

    private JsonParser() {

    }

    public static void init() {
        mapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
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

    public static User parseUser(String reader) {
        try {
            return mapper.reader(User.class).readValue(reader);
        } catch (IOException e) {
            log.error("Error parsing input string : {}", reader);
            log.error(e);
        }
        return null;
    }

    public static UserProfile parseProfile(String reader) {
        try {
            return mapper.reader(UserProfile.class).readValue(reader);
        } catch (IOException e) {
            log.error("Error parsing input string : {}", reader);
            log.error(e);
        }
        return null;
    }

    public static UserProfile parseProfile(InputStream reader) {
        try {
            return mapper.reader(UserProfile.class).readValue(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
