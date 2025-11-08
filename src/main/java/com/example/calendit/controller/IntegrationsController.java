package com.example.calendit.controller;

import com.example.calendit.model.User;
import com.example.calendit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/integrations")
@RequiredArgsConstructor
public class IntegrationsController {
    
    private final UserService userService;
    
    @GetMapping
    public String integrations(@AuthenticationPrincipal Object principal, Model model) {
        String email = null;
        User user = null;
        
        if (principal instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
        } else if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        }
        
        if (email != null) {
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                user = userOpt.get();
                model.addAttribute("user", user);
                
                // Check if Google is connected
                boolean googleConnected = user.getProvider().equals("GOOGLE");
                model.addAttribute("googleConnected", googleConnected);
                model.addAttribute("picture",user.getPicture());
                model.addAttribute("name",user.getName());
            }
        }
        
        return "integrations";
    }
}
