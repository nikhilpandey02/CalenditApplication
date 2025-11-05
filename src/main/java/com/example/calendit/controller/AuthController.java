package com.example.calendit.controller;

import com.example.calendit.model.User;
import com.example.calendit.service.PasswordResetService;
import com.example.calendit.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "signup";
        }

        try {
            User user = userService.registerUser(name, email, password);
            model.addAttribute("success", "Registration successful! Please log in.");
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        try {
            passwordResetService.sendResetEmail(email, "http://localhost:8080");
            model.addAttribute("message", "Password reset link has been sent to your email.");
        } catch (MessagingException e) {
            model.addAttribute("error", "Failed to send email. Please try again.");
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            model.addAttribute("token", token);
            return "reset-password";
        }

        boolean success = passwordResetService.resetPassword(token, password);
        if (success) {
            model.addAttribute("message", "Password successfully reset. You can now login.");
            return "login";
        } else {
            model.addAttribute("error", "Invalid or expired token.");
            return "reset-password";
        }
    }
}
