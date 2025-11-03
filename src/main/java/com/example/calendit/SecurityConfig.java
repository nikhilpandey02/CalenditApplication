package com.example.calendit;

import com.example.calendit.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // Crucial for the fix

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Spring will automatically inject your CustomOAuth2UserService here
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        // Explicitly use AntPathRequestMatcher to resolve ambiguity with H2 Console
                        .requestMatchers(
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/css/**"),
                                new AntPathRequestMatcher("/js/**"),
                                new AntPathRequestMatcher("/images/**"),
                                new AntPathRequestMatcher("/h2-console/**")
                              //  new AntPathRequestMatcher("/dashboard")
                        ).permitAll() // Allow these paths without authentication
                        .anyRequest().authenticated() // Secure all other requests
                )

                // Configure OAuth2 Login
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )

                // Configure Logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                )

                // Configure CSRF and Headers (Required for H2 Console access)
                .csrf(csrf -> csrf
                        // CSRF ignores MUST also use AntPathRequestMatcher
                        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                )
                .headers(headers -> headers
                        // Allows H2 Console to load in a frame
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }
}