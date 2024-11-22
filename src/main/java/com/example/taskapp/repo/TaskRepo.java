package com.example.taskapp.repo;

import com.example.taskapp.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findByUserUsername(String username);
    List<Task> findByStatus(String status);
    List<Task> findByCategoryId(Long categoryId);
    List<Task> findByStatusAndCategoryId(String status, Long categoryId);
}
