package com.pkg.tasktracker.controller;

import com.pkg.tasktracker.dto.AttachmentResponse;
import com.pkg.tasktracker.dto.DownloadableFile;
import com.pkg.tasktracker.exception.ForbiddenException;
import com.pkg.tasktracker.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService service;

    @PostMapping(path = {"/task/{taskId}/attachments", "/tasks/{taskId}/attachments"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponse> upload(@PathVariable Long taskId,
                                                     @RequestPart("file") MultipartFile file) throws ForbiddenException, BadRequestException {
        AttachmentResponse created = service.upload(taskId, file);
        return ResponseEntity.created(URI.create("/api/v1/attachments/" + created.id())).body(created);
    }

    @GetMapping({"/task/{taskId}/attachments", "/tasks/{taskId}/attachments"})
    public List<AttachmentResponse> list(@PathVariable Long taskId) throws ForbiddenException{
        return service.listForTask(taskId);
    }

    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws ForbiddenException, FileNotFoundException, BadRequestException {
        DownloadableFile f = service.getForDownload(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(f.contentType()))
                .contentLength(f.sizeBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(f.filename(), StandardCharsets.UTF_8)
                                .build().toString())
                .body(f.resource());
    }

    @DeleteMapping("/attachments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws ForbiddenException {
        service.delete(id);
    }
}
