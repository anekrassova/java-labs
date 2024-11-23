package com.example.taskapp.service;

import com.example.taskapp.entity.User;
import com.example.taskapp.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PasswordResetService {
    private final UserRepo userRepo;
    private final MailSender mailSender;
    @Autowired
    UserService userService;
    public PasswordResetService(UserRepo userRepo, MailSender mailSender) {
        this.userRepo = userRepo;
        this.mailSender = mailSender;
    }
    @Transactional
    public boolean sendPasswordResetLink(String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isEmpty()) {
            return false;
        }
        String resetLink = "http://localhost:8080/reset-password-form?email=" + email;
        String subject = "TaskApp: Password Reset Request";
        String content = "You have received this email because the application TaskApp has received a password reset request from your account. \n"
                +"To reset your password, please follow the link: \n"
                + resetLink + "\n"
                +"\nIf it wasn't you, then ignore this message.";
        mailSender.sendMail(email, subject, content);
        return true;
    }
    public boolean updatePassword(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(password);
            userService.saveUser(user);
            return true;
        }
        return false;
    }
}
