package com.example.taskapp.security;

import com.example.taskapp.service.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(encoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/registration", "/login", "/uploads/**","/reset-password","/reset-password-form").permitAll()
                        .requestMatchers("/admin/**", "/add-category").hasAuthority("ADMIN_ROLE")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler())
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll())
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/add-category", "/reset-password-form")
                );
        return http.build();
    }
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String role = authentication.getAuthorities().toString();
            if (role.contains("USER_ROLE")) {
                response.sendRedirect("/profile");
            } else {
                response.sendRedirect("/admin");
            }
        };
    }
}
