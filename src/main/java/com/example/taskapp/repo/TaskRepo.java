package com.example.taskapp.repo;

import com.example.taskapp.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findByUserUsername(String username);
    List<Task> findByStatus(String status);
    List<Task> findByCategoryId(Long categoryId);
    List<Task> findByStatusAndCategoryId(String status, Long categoryId);
    List<Task> findByUserId(Long userId);
    void deleteByCategoryId(Long categoryId);
    Page<Task> findByUserId(Long userId, Pageable pageable);
    Page<Task> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title, Pageable pageable);
    /*Page<Task> findByUsernameAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String username, String title, String description, Pageable pageable);*/

}
