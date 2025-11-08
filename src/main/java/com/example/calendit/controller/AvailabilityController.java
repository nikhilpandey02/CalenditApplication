package com.example.calendit.controller;

import com.example.calendit.model.AvailabilitySchedule;
import com.example.calendit.model.User;
import com.example.calendit.service.AvailabilityService;
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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final UserService userService;

    @GetMapping
    public String availability(@AuthenticationPrincipal Object principal, Model model) {
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
                List<AvailabilitySchedule> schedules = availabilityService.getAvailabilityByOwner(user);

                model.addAttribute("schedules", schedules);
                model.addAttribute("user", user);
                model.addAttribute("name", name);
                model.addAttribute("picture", picture);
                model.addAttribute("daysOfWeek", DayOfWeek.values());
                model.addAttribute("picture",user.getPicture());
                model.addAttribute("name",user.getName());
            }
        }

        return "availability";
    }

    @PostMapping("/add")
    public String addAvailability(@AuthenticationPrincipal Object principal,
                                  @RequestParam String dayOfWeek,
                                  @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
                                  @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
                                  RedirectAttributes redirectAttributes) {
        String email = null;

        if (principal instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
        } else if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        }

        // Validate end time > start time
        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            redirectAttributes.addFlashAttribute("error", "End time must be after start time!");
            return "redirect:/availability";
        }

        if (email != null) {
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                availabilityService.createAvailability(
                        userOpt.get(),
                        DayOfWeek.valueOf(dayOfWeek.toUpperCase()),
                        startTime,
                        endTime
                );
                redirectAttributes.addFlashAttribute("success", "Availability added successfully!");
            }
        }

        return "redirect:/availability";
    }

    @PostMapping("/delete/{id}")
    public String deleteAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        availabilityService.deleteAvailability(id);
        redirectAttributes.addFlashAttribute("success", "Availability deleted!");
        return "redirect:/availability";
    }

    @PostMapping("/toggle/{id}")
    public String toggleAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        availabilityService.toggleAvailability(id);
        redirectAttributes.addFlashAttribute("success", "Availability updated!");
        return "redirect:/availability";
    }
}
