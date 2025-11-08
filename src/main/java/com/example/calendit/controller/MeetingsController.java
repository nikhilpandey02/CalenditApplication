package com.example.calendit.controller;

import com.example.calendit.model.Slot;
import com.example.calendit.model.User;
import com.example.calendit.service.SlotService;
import com.example.calendit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingsController {
    
    private final SlotService slotService;
    private final UserService userService;
    
    @GetMapping
    public String meetings(@AuthenticationPrincipal Object principal,
                          @RequestParam(defaultValue = "upcoming") String tab,
                          Model model) {
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
                
                List<Slot> slots;
                if ("past".equals(tab)) {
                    slots = slotService.getPastSlots(user);
                } else {
                    slots = slotService.getUpcomingSlots(user);
                }
                
                model.addAttribute("slots", slots);
                model.addAttribute("activeTab", tab);
                model.addAttribute("user", user);
                model.addAttribute("picture",user.getPicture());
                model.addAttribute("name",user.getName());
            }
        }
        
        return "meetings";
    }
}
