package com.example.taskapp.controller;

import com.example.taskapp.entity.Category;
import com.example.taskapp.entity.Task;
import com.example.taskapp.entity.User;
import com.example.taskapp.service.CategoryService;
import com.example.taskapp.service.TaskService;
import com.example.taskapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;

    @GetMapping("/tasks/add")
    public String showAddTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "addTask";
    }

    @PostMapping("/tasks/add")
    public String addTask(@ModelAttribute Task task, @RequestParam Long categoryId) {
        task.setUser(userService.getCurrentUser());
        Category category = categoryService.getCategoryById(categoryId);
        if (category != null) {
            task.setCategory(category);
        } else {
            return "error";
        }
        task.setStatus("Scheduled");
        taskService.addTask(task);
        return "redirect:/";
    }

    @GetMapping("/tasks/delete")
    public String showDeleteTaskForm(Model model) {
        List<Task> tasks = taskService.getTasksByUsername(userService.getCurrentUser().getUsername());
        model.addAttribute("tasks", tasks);
        return "deleteTask";
    }

    @PostMapping("/tasks/delete")
    public String deleteTask(@RequestParam Long taskId) {
        taskService.deleteTaskById(taskId);
        return "redirect:/";
    }

    @GetMapping("/tasks/edit")
    public String showEditTaskForm(Model model) {
        List<Task> tasks = taskService.getTasksByUsername(userService.getCurrentUser().getUsername());
        model.addAttribute("tasks", tasks);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "editTask";
    }

    @GetMapping("/tasks/edit/{taskId}")
    @ResponseBody
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        Task task = taskService.getTaskById(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @PostMapping("/tasks/edit")
    public String editTask(@RequestParam Long taskId,
                           @RequestParam String description,
                           @RequestParam String dueDate,
                           @RequestParam String priority,
                           @RequestParam Long categoryId,
                           @RequestParam String status) {
        Task task = taskService.getTaskById(taskId);
        if (task == null) {
            return "error";
        }
        task.setDescription(description);
        task.setDueDate(LocalDateTime.parse(dueDate));
        task.setPriority(priority);
        task.setCategory(categoryService.getCategoryById(categoryId));
        task.setStatus(status);

        taskService.updateTask(task);
        return "redirect:/";
    }


}

