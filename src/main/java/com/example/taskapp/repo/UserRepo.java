package com.example.taskapp.repo;

import com.example.taskapp.entity.ERole;
import com.example.taskapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    public Optional<User> findByUsername(String username);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.username = :username WHERE u.id = :userId")
    void updateUsername(@Param("userId") Long userId, @Param("username") String username);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.email = :email WHERE u.id = :userId")
    void updateEmail(@Param("userId") Long userId, @Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.profilePhotoUrl = :profilePhotoUrl WHERE u.id = :userId")
    void updateProfilePhotoUrl(@Param("userId") Long userId, @Param("profilePhotoUrl") String profilePhotoUrl);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastUpdated = :lastUpdated WHERE u.id = :userId")
    void updateLastUpdated(@Param("userId") Long userId, @Param("lastUpdated") LocalDateTime lastUpdated);


    List<User> findAllByRole(ERole role);
    String findEmailById(Long id);
}
