package com.example.taskapp.controller;

import com.example.taskapp.entity.Category;
import com.example.taskapp.entity.Task;
import com.example.taskapp.entity.User;
import com.example.taskapp.service.CategoryService;
import com.example.taskapp.service.TaskService;
import com.example.taskapp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
@Controller
public class ProfileController {
    @Autowired
    UserService userService;
    @Autowired
    TaskService taskService;
    @Autowired
    CategoryService categoryService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(
            Model model,
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String searchQuery) {

        User user = userService.getCurrentUser();
        Long userId = user.getId();

        Pageable pageable = PageRequest.of(page, 10);

        Page<Task> tasksPage;
        if (searchQuery != null && !searchQuery.isBlank()) {
            tasksPage = taskService.searchTasksByUserIdAndTitle(userId, searchQuery, pageable);
        } else {
            tasksPage = taskService.getTasksByUserId(userId, pageable);
        }

        model.addAttribute("user", user);
        model.addAttribute("tasks", tasksPage.getContent()); // Задачи текущей страницы
        model.addAttribute("currentPage", page); // Номер текущей страницы
        model.addAttribute("totalPages", tasksPage.getTotalPages()); // Общее количество страниц
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("timestamp", System.currentTimeMillis());

        return "profile";
    }


    @GetMapping("/edit-profile")
    public String editProfilePage(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        return "edit-profile";
    }

    // Обработка изменения профиля
    @PostMapping("/edit-profile")
    public String editProfile(Principal principal,
                              @ModelAttribute User updatedUser,
                              @RequestParam("profilePhoto") MultipartFile profilePhoto,
                              Model model) {
        User user = userService.getCurrentUser();
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
            user.setUsername(updatedUser.getUsername());
            userService.updateUsername(user.getId(), updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
            user.setEmail(updatedUser.getEmail());
            userService.updateEmail(user.getId(), updatedUser.getEmail());
        }
        if (!profilePhoto.isEmpty()) {
            String photoPath = userService.saveProfilePhoto(profilePhoto);
            user.setProfilePhotoUrl(photoPath);  // Обновляем путь к фотографии
            userService.updateProfilePhotoUrl(user.getId(), photoPath);
        }
        model.addAttribute("user", user);
        return "redirect:/profile";
    }

}
