package cc.blynk.server.utils;

import cc.blynk.server.TestBase;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.model.auth.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: ddumanskiy
 * Date: 09.12.13
 * Time: 8:07
 */
public class FileManagerIntegrationTest extends TestBase {

    private User user1 = new User("name1", "pass1");
    private User user2 = new User("name2", "pass2");

    private FileManager fileManager = new FileManager(dataFolder);

    @Before
    public void cleanup() throws IOException {
        Path file;
        file = fileManager.generateFileName(user1.getName());
        Files.deleteIfExists(file);

        file = fileManager.generateFileName(user2.getName());
        Files.deleteIfExists(file);
    }

    @Test
    public void testGenerateFileName() {
        Path file = fileManager.generateFileName(user1.getName());
        assertEquals("u_name1.user", file.getFileName().toString());
    }

    @Test
    public void testCreationTempFile() throws IOException {
        fileManager.overrideUserFile(user1);
        //file existence ignored
        fileManager.overrideUserFile(user1);
    }

    @Test
    public void testReadListOfFiles() throws IOException {
        fileManager.overrideUserFile(user1);
        fileManager.overrideUserFile(user2);

        Map<String, User> users = fileManager.deserialize();
        assertNotNull(users);
        assertNotNull(users.get(user1.getName()));
        assertNotNull(users.get(user2.getName()));
    }

    @Test
    public void testOverrideFiles() throws IOException {
        fileManager.overrideUserFile(user1);
        fileManager.overrideUserFile(user1);

        Map<String, User> users = fileManager.deserialize();
        assertNotNull(users);
        assertNotNull(users.get(user1.getName()));
    }

}
