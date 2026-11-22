package com.pkg.tasktracker.repository;

import com.pkg.tasktracker.entity.Attachment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    @EntityGraph(attributePaths = "uploadedBy")   // avoids N+1 when we read uploader's name
    List<Attachment> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    long countByTaskId(Long taskId);

    void deleteByTaskId(Long taskId);
}