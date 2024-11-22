package com.example.taskapp.service;

import com.example.taskapp.entity.Task;
import com.example.taskapp.repo.TaskRepo;
import com.example.taskapp.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    TaskRepo taskRepo;
    @Autowired
    UserRepo userRepo;
    public List<Task> getTasksByUsername(String username){
        return taskRepo.findByUserUsername(username);
    }
    public Task addTask(Task task){
        return taskRepo.save(task);
    }
    public void deleteTaskById(Long taskId) {
        if (taskRepo.existsById(taskId)) {
            taskRepo.deleteById(taskId);
        } else {
            throw new IllegalArgumentException("Task with ID " + taskId + " does not exist");
        }
    }
    public Task getTaskById(Long taskId) {
        return taskRepo.findById(taskId).get();
    }
    public Task updateTask(Task task) {
        return taskRepo.save(task);
    }
    public List<Task> getFilteredTasks(String status, Long categoryId, List<Task> tasks) {
        if ("".equals(status)) {
            status = null;
        }

        List<Task> filteredTasks = new ArrayList<>();

        for (Task task : tasks) {
            boolean matchesStatus = (status == null || task.getStatus().equals(status));
            boolean matchesCategory = (categoryId == null || task.getCategory().getId().equals(categoryId));

            if (matchesStatus && matchesCategory) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepo.findByUserId(userId);
    }
    @Transactional
    public void deleteTasksByCategoryId(Long categoryId) {
        // Удаляем все задачи, у которых категория соответствует переданному ID
        taskRepo.deleteByCategoryId(categoryId); // Предполагаем, что у вас есть соответствующий метод в репозитории
    }
    public Page<Task> getTasksByUserId(Long userId, Pageable pageable) {
        return taskRepo.findByUserId(userId, pageable);
    }
    public Page<Task> searchTasksByUserIdAndTitle(Long userId, String title, Pageable pageable) {
        return taskRepo.findByUserIdAndTitleContainingIgnoreCase(userId, title, pageable);
    }
    /*public Page<Task> searchTasksByUsernameAndQuery(String username, String query, Pageable pageable) {
        return taskRepo.findByUsernameAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                username, query, query, pageable);
    }*/

}
