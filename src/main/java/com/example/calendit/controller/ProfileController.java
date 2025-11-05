package com.example.calendit.controller;

import com.example.calendit.model.User;
import com.example.calendit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    public String showProfilePage(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String email = null;
        String name = null;
        String picture = null;

        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
            picture = oAuth2User.getAttribute("picture");

            // Ensure user record exists or gets updated
            User user = userService.saveOrUpdateUser(email, name, picture, oAuth2User.getName()).orElse(null);
            model.addAttribute("user", user);

        } else {
            email = authentication.getName();
            User user = userService.findByEmail(email).orElse(null);

            if (user == null) {
                return "redirect:/login?error=usernotfound";
            }

            model.addAttribute("user", user);
        }

        return "profile";
    }
}
