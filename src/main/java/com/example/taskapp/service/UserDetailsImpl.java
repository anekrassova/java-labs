package com.example.taskapp.service;

import com.example.taskapp.entity.User;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private LocalDateTime creationDate;
    private Collection<? extends GrantedAuthority> authorities;

    // Добавляем поле User
    private User user;

    public UserDetailsImpl(Long id, String username, String password, LocalDateTime creationDate, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.creationDate = creationDate;
        this.authorities = authorities;
    }

    // Новый конструктор для инициализации User
    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.creationDate = LocalDateTime.now(); // или используйте user.getCreationDate(), если у вас есть это поле
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        this.user = user; // Инициализируем поле user
    }

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(user);
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
