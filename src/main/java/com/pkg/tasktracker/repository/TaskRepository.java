package com.pkg.tasktracker.repository;

import com.pkg.tasktracker.entity.Task;
import com.pkg.tasktracker.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("""
            select t from Task t
            where t.project.id = :projectId
              and (:status is null or t.status = :status)
              and (:assigneeId is null or t.assignee.id = :assigneeId)
              and (:q is null or lower(t.title) like lower(concat('%', :q, '%'))
                    or lower(coalesce(t.description, '')) like lower(concat('%', :q, '%')))
              and (:dueBefore is null or t.dueDate <= :dueBefore)
            """)
    Page<Task> searchProjectTasks(@Param("projectId") Long projectId,
                                  @Param("status") TaskStatus status,
                                  @Param("assigneeId") Long assigneeId,
                                  @Param("q") String q,
                                  @Param("dueBefore") LocalDate dueBefore,
                                  Pageable pageable);

    @Query("""
            select t from Task t
            where t.assignee.id = :userId
              and (:status is null or t.status = :status)
            """)
    Page<Task> findMine(@Param("userId") Long userId,
                        @Param("status") TaskStatus status,
                        Pageable pageable);
}
