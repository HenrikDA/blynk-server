package cc.blynk.server.dao;

import cc.blynk.common.utils.Config;
import cc.blynk.server.model.auth.User;
import cc.blynk.server.utils.Finder;
import cc.blynk.server.utils.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 6:53 PM
 */
public final class FileManager {

    private static final Logger log = LogManager.getLogger(FileManager.class);

    private Path dataDir;

    public FileManager(String dataFolder) {
        try {
            this.dataDir = createDatadir(dataFolder);
        } catch (RuntimeException e) {
            this.dataDir = createDatadir(System.getProperty("java.io.tmpdir"));
        }

        log.info("Data dir created '{}'", dataDir);
    }

    private static Path createDatadir(String dataFolder) {
        Path dataDir = Paths.get(dataFolder);
        try {
            Files.createDirectories(dataDir);
        } catch (IOException ioe) {
            log.error("Error creating data folder '{}'", dataFolder);
            throw new RuntimeException("Error creating data folder '" + dataFolder + "'");
        }
        return dataDir;
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

    public void overrideUserFile(User user) throws IOException {
        Path file = generateFileName(user.getName());
        try (BufferedWriter writer = Files.newBufferedWriter(file, Config.DEFAULT_CHARSET)) {
            String userString = user.toString();

            writer.write(userString);
        }
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
