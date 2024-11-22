package com.example.taskapp.controller;

import com.example.taskapp.entity.Category;
import com.example.taskapp.entity.Task;
import com.example.taskapp.service.CategoryService;
import com.example.taskapp.service.TaskService;
import com.example.taskapp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Controller
public class MainController {
    @Autowired
    UserService userService;
    @Autowired
    TaskService taskService;
    @Autowired
    CategoryService categoryService;

    @GetMapping("/")
    public String main(Model model, Principal principal) {
        String username = principal.getName();
        List<Task> userTasks = taskService.getTasksByUsername(username);
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("tasks", userTasks);
        model.addAttribute("categories", categories);
        return "main";
    }

    @GetMapping("/tasks")
    public String getTasks(
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            Model model) {

        List<Task> tasks = taskService.getTasksByUsername(userService.getCurrentUser().getUsername());
        List<Category> categories = categoryService.getAllCategories();

        if (status != null || categoryId != null) {
            tasks = taskService.getFilteredTasks(status, categoryId, tasks);
        }

        if ("date".equals(sort)) {
            tasks.sort(Comparator.comparing(Task::getDueDate));
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("categories", categories);
        return "main";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}