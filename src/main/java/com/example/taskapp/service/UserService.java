package com.example.taskapp.service;

import com.example.taskapp.entity.ERole;
import com.example.taskapp.entity.User;
import com.example.taskapp.repo.UserRepo;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    UserRepo userRepo;
    private BCryptPasswordEncoder encoder(){return new BCryptPasswordEncoder();}
    public void saveUser(User user){
        user.setPassword(encoder().encode(user.getPassword()));
        userRepo.save(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getUser();
        }

        throw new IllegalArgumentException("No authenticated user found");
    }

    private final String UPLOAD_DIR = ".../resources/static/images/";

    public String saveProfilePhoto(MultipartFile profilePhoto) {
        try {
            String projectPath = System.getProperty("user.dir");

            Path uploadPath = Paths.get(projectPath, "src/main/resources/static/images/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis() + "_" + profilePhoto.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            profilePhoto.transferTo(filePath.toFile());

            Path targetPath = Paths.get(projectPath, "target/classes/static/images/");
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }
            Path targetFilePath = targetPath.resolve(fileName);
            Files.copy(filePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);

            return "/images/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void updateUsername(Long userId, String newUsername) {
        userRepo.updateUsername(userId, newUsername);
    }

    public void updateEmail(Long userId, String newEmail) {
        userRepo.updateEmail(userId, newEmail);
    }

    public void updateProfilePhotoUrl(Long userId, String newProfilePhotoUrl) {
        userRepo.updateProfilePhotoUrl(userId, newProfilePhotoUrl);
        User user = userRepo.findById(userId).get();
        userRepo.updateLastUpdated(userId, LocalDateTime.now());
    }

    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public List<User> getUsersWithUserRole() {
        return userRepo.findAllByRole(ERole.USER_ROLE); // Используем метод репозитория
    }
    public String getEmailByUserId(Long userId) {
        System.out.println("Looking for user with id: " + userId);
        User user = userRepo.findById(userId).orElse(null);
        if (user != null) {
            System.out.println("User found: " + user.getEmail());
            return user.getEmail();
        } else {
            System.out.println("User not found for id: " + userId);
        }
        return null;
    }

}
