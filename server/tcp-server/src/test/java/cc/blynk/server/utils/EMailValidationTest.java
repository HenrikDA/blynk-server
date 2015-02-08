package cc.blynk.server.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 6:43 PM
 */
public class EMailValidationTest {

    @Test
    public void testAllValid() {
        String[] mailList = new String[] {
                "1@mail.ru",
                "google@gmail.com",
                "dsasd234e021-0+@mail.ua",
                "ddd@yahoo.com",
                "mmmm@yahoo.com",
                "mmmmm-100@yahoo.com",
                "mmmmm.100@yahoo.com",
                "mmmm111@mmmm.com",
                "mmmm-100@mmmm.net",
                "mmmm.100@mmmm.com.au",
                "mmmm@1.com",
                "mmmm@gmail.com.com",
                "mmmm+100@gmail.com",
                "mmmm-100@yahoo-test.com"
        };

        for (String email : mailList) {
            assertTrue(EMailValidator.isValid(email));
        }
    }

    @Test
    public void testAllInValid() {
        String[] mailList = new String[] {
                "mmmm",
                "mmmm@.com.my",
                "mmmm123@.com",
                "mmmm123@.com.com",
                ".mmmm@mmmm.com",
                "mmmm()*@gmail.com",
                "mmmm..2002@gmail.com",
                "mmmm.@gmail.com",
                "mmmm@mmmm@gmail.com"
        };

        for (String email : mailList) {
            assertFalse(EMailValidator.isValid(email));
        }
    }

}
