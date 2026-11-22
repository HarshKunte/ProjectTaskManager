package com.pkg.tasktracker.controller.helper;

import com.pkg.tasktracker.exception.StorageException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class ContentTypeDetector {

    private final Tika tika = new Tika();

    /** Filename is only a hint for formats that share a signature (e.g. docx is a zip). */
    public String detect(MultipartFile file, String filenameHint) {
        try (InputStream in = file.getInputStream()) {
            return tika.detect(in, filenameHint);
        } catch (IOException e) {
            throw new StorageException("Could not read upload", e);
        }
    }
}