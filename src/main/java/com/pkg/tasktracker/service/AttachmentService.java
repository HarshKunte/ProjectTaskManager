package com.pkg.tasktracker.service;

import com.pkg.tasktracker.common.TransactionUtils;
import com.pkg.tasktracker.config.StorageProperties;
import com.pkg.tasktracker.controller.helper.ContentTypeDetector;
import com.pkg.tasktracker.controller.helper.CurrentUser;
import com.pkg.tasktracker.dto.AttachmentResponse;
import com.pkg.tasktracker.dto.DownloadableFile;
import com.pkg.tasktracker.entity.Attachment;
import com.pkg.tasktracker.entity.Task;
import com.pkg.tasktracker.entity.User;
import com.pkg.tasktracker.exception.ForbiddenException;
import com.pkg.tasktracker.exception.UnsupportedFileTypeException;
import com.pkg.tasktracker.repository.AttachmentRepository;
import com.pkg.tasktracker.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachments;
    private final TaskRepository tasks;
    private final StorageService storage;
    private final StorageProperties props;
    private final ContentTypeDetector detector;
    private final AccessService access;
    private final CurrentUser currentUser;

    // ---------- upload ----------

    @Transactional
    public AttachmentResponse upload(Long taskId, MultipartFile file) throws ForbiddenException, BadRequestException{
        User me = currentUser.get();
        Task task = tasks.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        access.requireMember(task.getProject().getId(), me.getId());

        String filename = sanitizeFilename(file.getOriginalFilename());
        validate(file, taskId);

        String detectedType = detector.detect(file, filename);
        if (!props.allowedContentTypes().contains(detectedType)) {
            throw new UnsupportedFileTypeException("File type not allowed: " + detectedType);
        }

        String storedName = storage.store(file);
        // If anything later fails and the DB transaction rolls back, don't leave an orphan file.
        TransactionUtils.afterRollback(() -> storage.deleteQuietly(storedName));

        Attachment a = new Attachment();
        a.setTask(task);
        a.setUploadedBy(me);
        a.setOriginalFilename(filename);
        a.setStoredFilename(storedName);
        a.setContentType(detectedType);
        a.setSizeBytes(file.getSize());
        a = attachments.save(a);

        return AttachmentResponse.from(a);
    }

    private void validate(MultipartFile file, Long taskId) throws BadRequestException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        if (file.getSize() > props.maxFileSizeBytes()) {
            throw new BadRequestException("File exceeds the maximum size");
        }
        if (attachments.countByTaskId(taskId) >= props.maxFilesPerTask()) {
            throw new BadRequestException(
                    "This task already has the maximum of " + props.maxFilesPerTask() + " attachments");
        }
    }

    /** Display-only name: strips directories, control characters, and caps the length. */
    private String sanitizeFilename(String original) {
        if (original == null) return "file";
        String name = StringUtils.getFilename(StringUtils.cleanPath(original));
        if (name == null) return "file";
        name = name.replaceAll("\\p{Cntrl}", "").trim();
        if (name.isEmpty() || name.equals("..") || name.equals(".")) return "file";
        return name.length() > 255 ? name.substring(0, 255) : name;
    }

    // ---------- list ----------

    @Transactional
    public List<AttachmentResponse> listForTask(Long taskId) throws ForbiddenException{
        Task task = tasks.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        access.requireMember(task.getProject().getId(), currentUser.get().getId());
        return attachments.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(AttachmentResponse::from)
                .toList();
    }

    // ---------- download ----------

    @Transactional
    public DownloadableFile getForDownload(Long attachmentId) throws ForbiddenException, FileNotFoundException, BadRequestException {
        Attachment a = find(attachmentId);
        access.requireMember(a.getTask().getProject().getId(), currentUser.get().getId());
        Resource resource = storage.load(a.getStoredFilename());
        return new DownloadableFile(resource, a.getOriginalFilename(), a.getContentType(), a.getSizeBytes());
    }

    // ---------- delete ----------

    @Transactional
    public void delete(Long attachmentId) throws ForbiddenException {
        User me = currentUser.get();
        Attachment a = find(attachmentId);
        Long projectId = a.getTask().getProject().getId();

        access.requireMember(projectId, me.getId());
        boolean isUploader = a.getUploadedBy().getId().equals(me.getId());
        if (!isUploader && !access.isOwner(projectId, me.getId())) {
            throw new ForbiddenException("Only the uploader or the project owner can delete this attachment");
        }

        attachments.delete(a);
        String storedName = a.getStoredFilename();
        // Delete the file only once the DB row is definitely gone.
        TransactionUtils.afterCommit(() -> storage.deleteQuietly(storedName));
    }

    /** Call this from TaskService.delete(...) BEFORE deleting the task itself. */
    @Transactional
    public void deleteAllForTask(Long taskId) {
        List<String> storedNames = attachments.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(Attachment::getStoredFilename)
                .toList();
        attachments.deleteByTaskId(taskId);
        TransactionUtils.afterCommit(() -> storedNames.forEach(storage::deleteQuietly));
    }

    private Attachment find(Long id) {
        return attachments.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));
    }
}
