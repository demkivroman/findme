package org.demkiv.domain.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

@Slf4j
public class TempDirectory implements AutoCloseable {

    private final Path directory;

    public TempDirectory(String prefix) throws IOException {
        this.directory = Files.createTempDirectory(prefix);
    }

    public Path getPath() {
        return directory;
    }

    @Override
    public void close() throws IOException {
        deleteDirectoryRecursively(directory);
    }

    private void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> entries = Files.walk(path)) {
                entries.sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                log.error("Directory autodelete. Failed to delete file {}", p, e);
                            }
                        });
            }
        }
    }
}
