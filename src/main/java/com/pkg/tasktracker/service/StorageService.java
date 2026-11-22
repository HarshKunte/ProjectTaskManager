package com.pkg.tasktracker.service;

import com.pkg.tasktracker.config.StorageProperties;
import com.pkg.tasktracker.exception.StorageException;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class StorageService {

    private final Path root;

    public StorageService(StorageProperties props) {
        this.root = Path.of(props.uploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create upload directory " + root, e);
        }
    }

    /** Saves the upload under a random name and returns that name. */
    public String store(MultipartFile file) throws BadRequestException{
        String storedName = UUID.randomUUID().toString();
        Path target = resolveSafely(storedName);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target);                       // fails if it already exists (never overwrites)
        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }
        return storedName;
    }

    public Resource load(String storedName) throws FileNotFoundException, BadRequestException {
        Path p = resolveSafely(storedName);
        if (!Files.isRegularFile(p) || !Files.isReadable(p)) {
            throw new FileNotFoundException("File content not found");
        }
        return new FileSystemResource(p);
    }

    public void delete(String storedName) throws IOException {
        Files.deleteIfExists(resolveSafely(storedName));
    }

    /** Used in cleanup callbacks, where a failure must not break the request. */
    public void deleteQuietly(String storedName) {
        try {
            delete(storedName);
        } catch (IOException | RuntimeException e) {
            // TODO: log a warning; a periodic cleanup job can sweep orphaned files
        }
    }

    /** Guards against path traversal like "../../etc/passwd". */
    private Path resolveSafely(String storedName) throws BadRequestException {
        Path p = root.resolve(storedName).normalize();
        if (!p.startsWith(root) || p.equals(root)) {
            throw new BadRequestException("Invalid file path");
        }
        return p;
    }
}
