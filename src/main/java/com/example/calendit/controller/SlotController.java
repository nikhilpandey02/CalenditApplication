package com.example.calendit.controller;

import com.example.calendit.model.Slot;
import com.example.calendit.model.User;
import com.example.calendit.service.SlotService;
import com.example.calendit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;
    private final UserService userService;

    @GetMapping("/add")
    public String addSlotForm(Model model) {
        return "add-slot";
    }

    @PostMapping("/add")
    public String addSlot(@AuthenticationPrincipal Object principal,
                          @RequestParam String meetingName,
                          @RequestParam int durationMinutes,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                          @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time,
                          RedirectAttributes redirectAttributes) {

        String email = null;
        User user = null;

        // ✅ Handle Google OAuth2 login
        if (principal instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String picture = oAuth2User.getAttribute("picture");
            String googleId = oAuth2User.getAttribute("sub");

            user = userService.saveOrUpdateUser(email, name, picture, googleId).orElse(null);
        }
        // ✅ Handle manual form login
        else if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
            user = userService.findByEmail(email).orElse(null);
        }

        if (user != null) {
            slotService.createSlot(user, meetingName, durationMinutes, date, time);
            redirectAttributes.addFlashAttribute("success", "Meeting slot added successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to find logged-in user.");
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/list")
    public String listSlots(@RequestParam String owner, Model model) {
        Optional<User> userOpt = userService.findByEmail(owner);

        if (userOpt.isPresent()) {
            List<Slot> slots = slotService.getAvailableSlots(userOpt.get());
            model.addAttribute("slots", slots);
            model.addAttribute("ownerEmail", owner);
            model.addAttribute("ownerName", userOpt.get().getName());
        }

        return "slot-list";
    }

    @PostMapping("/remove")
    public String removeSlot(@RequestParam Long slotId, RedirectAttributes redirectAttributes) {
        slotService.deleteSlot(slotId);
        redirectAttributes.addFlashAttribute("success", "Slot removed successfully!");
        return "redirect:/dashboard";
    }
}
