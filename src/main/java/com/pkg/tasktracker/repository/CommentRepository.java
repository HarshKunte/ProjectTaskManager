package com.pkg.tasktracker.repository;

import com.pkg.tasktracker.entity.Comment;
import com.pkg.tasktracker.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByTask(Task task, Pageable pageable);
}
