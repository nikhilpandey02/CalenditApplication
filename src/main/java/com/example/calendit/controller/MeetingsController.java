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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        String name = null;
        String picture = null;
        User user = null;

        if (principal instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
            picture = oAuth2User.getAttribute("picture");
        } else if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        }

        if (email != null) {
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                user = userOpt.get();
                LocalDateTime now = LocalDateTime.now();

                List<Slot> allBookedSlots = slotService.getAllSlotsByOwner(user)
                        .stream()
                        .filter(slot -> !slot.isAvailable() && slot.getBooking() != null)
                        .collect(Collectors.toList());

                List<Slot> slots;
                if ("past".equals(tab)) {
                    slots = allBookedSlots.stream()
                            .filter(slot -> {
                                LocalDateTime meetingEnd = LocalDateTime.of(slot.getDate(),
                                        slot.getTime().plusMinutes(slot.getDurationMinutes()));
                                return meetingEnd.isBefore(now);
                            })
                            .collect(Collectors.toList());
                } else {
                    slots = allBookedSlots.stream()
                            .filter(slot -> {
                                LocalDateTime meetingEnd = LocalDateTime.of(slot.getDate(),
                                        slot.getTime().plusMinutes(slot.getDurationMinutes()));
                                return !meetingEnd.isBefore(now);
                            })
                            .collect(Collectors.toList());
                }

                model.addAttribute("slots", slots);
                model.addAttribute("activeTab", tab);
                model.addAttribute("user", user);
                model.addAttribute("name", name);
                model.addAttribute("picture", picture);
                model.addAttribute("email", email);
            }
        }

        return "meetings";
    }
}
