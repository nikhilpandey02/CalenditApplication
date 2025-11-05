package com.example.calendit.service;

import com.example.calendit.model.User;
import com.example.calendit.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public void sendResetEmail(String email, String appUrl) throws MessagingException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15)); // valid for 15 mins
        userRepository.save(user);

        String resetUrl = appUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request - Calendit");
        message.setText("Hi " + user.getName() + ",\n\n" +
                "Click the link below to reset your password:\n" + resetUrl +
                "\n\nIf you didn’t request this, please ignore this email.");

        mailSender.send(message);
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return false; // token expired
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
        return true;
    }
}
