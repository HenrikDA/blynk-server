package cc.blynk.server.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Utility class to walk through all files within folder by pattern
 *
 * User: ddumanskiy
 * Date: 09.12.13
 * Time: 7:51
 */
public class Finder extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher;
    private final List<Path> foundFiles = new ArrayList<>();

    Finder(String pattern) {
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
    }

    // Compares the glob pattern against
    // the file or directory name.
    private void find(Path file) {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            foundFiles.add(file);
        }
    }

    // Invoke the pattern matching method on each file.
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        find(file);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }

    public List<Path> getFoundFiles() {
        return foundFiles;
    }
}