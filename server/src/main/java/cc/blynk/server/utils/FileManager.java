package cc.blynk.server.utils;

import cc.blynk.common.utils.Config;
import cc.blynk.server.auth.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 6:53 PM
 */
public final class FileManager {

    private static final Logger log = LogManager.getLogger(FileManager.class);

    private final Path dataDir;

    public FileManager() {
        Properties properties = new Properties();
        try (InputStream is = FileManager.class.getResourceAsStream("/server.properties")) {
            properties.load(is);
        } catch (IOException ioe) {
            log.error("Cannot read server.properties file.");
        }

        String dataFolder = properties.getProperty("data.folder") == null ?
                System.getProperty("java.io.tmpdir") :
                properties.getProperty("data.folder");

        dataDir = Paths.get(dataFolder);
        try {
            Files.createDirectories(dataDir);
        } catch (IOException ioe) {
            log.error("Error creating data folders '{}'", dataFolder);
            throw new RuntimeException("Error creating data folders.");
        }
        log.info("Data dir {}", dataFolder);
    }

    private static User readUserFromFile(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, Config.DEFAULT_CHARSET)) {
            String userString = reader.readLine();
            return JsonParser.parseUser(userString);
        } catch (IOException ioe) {
            log.error("Error reading temp file.", ioe);
        }

        return null;
    }

    public Path getDataDir() {
        return dataDir;
    }

    public Path generateFileName(String userName) {
        return Paths.get(dataDir.toString(), "u_" + userName + ".user");
    }

    /**
     * Returns true if user was successfully saved to file.
     * @param user - user to save
     * @return true in case of success
     */
    public boolean saveNewUserToFile(User user) {
        createFile(user.getName());

        return overrideUserFile(user);
    }

    private void createFile(String userName) {
        try {
            Path file = generateFileName(userName);
            Files.createFile(file);
        } catch (FileAlreadyExistsException fae) {
            log.error("File already exists. Should never happen. User : {}", userName);
        } catch (IOException ioe) {
            log.error("Error creating temp file.", ioe);
        }
    }

    public boolean overrideUserFile(User user) {
        Path file = generateFileName(user.getName());
        try (BufferedWriter writer = Files.newBufferedWriter(file, Config.DEFAULT_CHARSET)) {
            writer.write(user.toString());
        } catch (IOException ioe) {
            log.error("Error writing file.", ioe);
            return false;
        }

        return true;
    }

    public ConcurrentHashMap<String, User> deserialize() {
        Finder finder = new Finder("u_*.user");


        try {
            Files.walkFileTree(dataDir, finder);
        } catch (IOException e) {
            log.error("Error reading tmp files.", e);
        }

        ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
        for (Path path : finder.getFoundFiles()) {
            User user = readUserFromFile(path);
            if (user != null) {
                users.putIfAbsent(user.getName(), user);
            }
        }

        return users;
    }

}
