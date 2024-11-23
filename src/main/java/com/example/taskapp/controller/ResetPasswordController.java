package com.example.taskapp.controller;

import com.example.taskapp.entity.User;
import com.example.taskapp.repo.UserRepo;
import com.example.taskapp.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ResetPasswordController {
    @Autowired
    private UserRepo userRepo;
    private final PasswordResetService passwordResetService;
    public ResetPasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }
    @GetMapping("/reset-password")
    public String resetPasswordForm() {
        return "reset-password";
    }
    @PostMapping("/reset-password")
    public String sendResetPassword(@RequestParam String email, Model model) {
        boolean emailSent = passwordResetService.sendPasswordResetLink(email);
        if (!emailSent) {
            model.addAttribute("error", "This email address does not exist.");
            return "reset-password";
        }
        model.addAttribute("success", "The reset password link has been sent.");
        return "redirect:/login";
    }

    @GetMapping("/reset-password-form")
    public String passwordResetForm(@RequestParam String email, Model model) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Ссылка для сброса пароля недействительна.");
            return "reset-password";
        }
        System.out.println("User found:" + userOptional.get().getUsername());
        model.addAttribute("email", email);
        return "new-password";
    }

    @PostMapping("/reset-password-form")
    public String updatePassword(@RequestParam String email, @RequestParam String newPassword, Model model) {
        System.out.println("Trying to update password for email: " + email );
        boolean updated = passwordResetService.updatePassword(email, newPassword);
        if (updated) {
            return "redirect:/login?resetSuccess";
        } else {
            model.addAttribute("error", "Не удалось обновить пароль. Попробуйте еще раз.");
            return "new-password";
        }
    }
}
