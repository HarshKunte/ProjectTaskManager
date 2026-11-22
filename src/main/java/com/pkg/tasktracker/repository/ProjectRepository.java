package com.pkg.tasktracker.repository;

import com.pkg.tasktracker.entity.Project;
import com.pkg.tasktracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwner(User owner);
}
