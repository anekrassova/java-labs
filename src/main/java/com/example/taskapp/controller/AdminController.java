package com.example.taskapp.controller;

import com.example.taskapp.service.MailSender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.taskapp.entity.Category;
import com.example.taskapp.entity.Task;
import com.example.taskapp.entity.User;
import com.example.taskapp.service.CategoryService;
import com.example.taskapp.service.TaskService;
import com.example.taskapp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    UserService userService;
    @Autowired
    TaskService taskService;
    @Autowired
    CategoryService categoryService;
    private final MailSender mailSender;

    @Autowired
    public AdminController(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        List<User> usersWithUserRole = userService.getUsersWithUserRole();
        model.addAttribute("users", usersWithUserRole);
        return "admin";
    }

    @GetMapping("/users-tasks/{userId}")
    public String editTasksPage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String searchQuery,
            Model model) {
        User user = userService.getUserById(userId);
        Pageable pageable = PageRequest.of(page, 10);

        Page<Task> tasksPage;
        if (searchQuery != null && !searchQuery.isBlank()) {
            tasksPage = taskService.searchTasksByUserIdAndTitle(userId, searchQuery, pageable);
        } else {
            tasksPage = taskService.getTasksByUserId(userId, pageable);
        }

        model.addAttribute("user", user);
        model.addAttribute("tasks", tasksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tasksPage.getTotalPages());
        model.addAttribute("searchQuery", searchQuery);

        return "edit-tasks-admin";
    }


    @GetMapping("/delete-task/{taskId}")
    public String deleteTask(@PathVariable Long taskId, Model model) {
        try {
            Long userId = taskService.getTaskById(taskId).getUser().getId();
            // данные о задаче для удаления
            String to = userService.getUserById(userId).getEmail();
            String subject = "Task Deletion";
            String content = "Your task " + taskService.getTaskById(taskId).getTitle() + " has been deleted!";
            taskService.deleteTaskById(taskId);
            mailSender.sendMail(to, subject, content);
            return "redirect:/users-tasks/" + userId;
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete task: " + e.getMessage());
            return "error";
        }
    }
    @GetMapping("/edit-task/{taskId}")
    public String editTaskPage(@PathVariable Long taskId, Model model) {
        Task task = taskService.getTaskById(taskId);
        List<Category> categories = categoryService.getAllCategories();

        //отладка
        System.out.println("Task loaded: " + task);
        if (task != null && task.getUser() != null) {
            System.out.println("Associated User: " + task.getUser().getId());
            System.out.println("No user associated with the task");
        }
        //конец отладки

        String formattedDueDate = task.getDueDate().toString();
        model.addAttribute("task", task);
        model.addAttribute("categories", categories);
        model.addAttribute("formattedDueDate", formattedDueDate);
        return "edit-task";
    }
    @PostMapping("/update-task")
    public String updateTask(@ModelAttribute Task task, Model model) {
        try {
            User user = task.getUser();
            //отладка
            System.out.println("Task loaded: " + task);
            if (task != null && task.getUser() != null) {
                Long userId = user.getId();
                System.out.println("Associated User: " + userId);
                System.out.println("data type of user id:" + task.getUser().getId().getClass().getName());
                String to = userService.getEmailByUserId(userId);
                System.out.println("email: " + to);
                String subject = "Task Editing";
                String content = "Your task: " + task.getTitle() + " has been edited";
                mailSender.sendMail(to, subject, content);
            } else {
                System.out.println("No user associated with the task");
            }
            taskService.updateTask(task);
            return "redirect:/users-tasks/" + task.getUser().getId();
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update task: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/add-task/{userId}")
    public String addTaskPage(@PathVariable Long userId, Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("userId", userId);
        return "addTask";
    }
    @PostMapping("/add-task/{userId}")
    public String addTask(@PathVariable Long userId,
                          @RequestParam String title,
                          @RequestParam String description,
                          @RequestParam String dueDate,
                          @RequestParam String priority,
                          @RequestParam Long categoryId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return "redirect:/error";
        }
        LocalDateTime dueDateTime = LocalDateTime.parse(dueDate);
        Category category = categoryService.getCategoryById(categoryId);
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDateTime);
        task.setPriority(priority);
        task.setUser(user);
        task.setCategory(category);
        task.setStatus("Pending");
        String to = userService.getUserById(userId).getEmail();
        String subject = "New task have been added.";
        String content = task.getTitle() + " (" + task.getDescription() + ") have been assigned to you. \n"
                + "Due date: " + task.getDueDate();
        taskService.addTask(task);
        mailSender.sendMail(to, subject, content);

        return "redirect:/users-tasks/{userId}";
    }
    @GetMapping("/manage-categories")
    public String manageCategoriesPage(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "manage-categories";
    }

    @GetMapping("/delete-category/{categoryId}")
    public String deleteCategory(@PathVariable Long categoryId) {
        taskService.deleteTasksByCategoryId(categoryId);
        categoryService.deleteCategoryById(categoryId);
        return "redirect:/manage-categories";
    }

    @GetMapping("/add-category")
    public String addCategoryPage() {
        return "add-category";
    }

    @PostMapping("/add-category")
    public String addCategory(@RequestParam String title) {
        Category category = new Category();
        category.setName(title);
        categoryService.addCategory(category);
        return "redirect:/manage-categories";
    }
}