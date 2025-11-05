//package com.example.calendit.controller;
//
//import com.example.calendit.model.Booking;
//import com.example.calendit.model.Slot;
//import com.example.calendit.model.User;
//import com.example.calendit.service.BookingService;
//import com.example.calendit.service.SlotService;
//import com.example.calendit.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import java.util.List;
//import java.util.Optional;
//
//@Controller
//@RequiredArgsConstructor
//public class DashboardController {
//
//    private final UserService userService;
//    private final SlotService slotService;
//    private final BookingService bookingService;
//
//    @GetMapping("/dashboard")
//    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
//        String email = principal.getAttribute("email");
//        String name = principal.getAttribute("name");
//        String picture = principal.getAttribute("picture");
//
//        Optional<User> userOpt = userService.findByEmail(email);
//
//        if (userOpt.isPresent()) {
//            User user = userOpt.get();
//            List<Slot> mySlots = slotService.getAllSlotsByOwner(user);
//            List<Booking> myBookings = bookingService.getBookingsByUser(user);
//
//            model.addAttribute("user", user);
//            model.addAttribute("name", name);
//            model.addAttribute("picture", picture);
//            model.addAttribute("mySlots", mySlots);
//            model.addAttribute("myBookings", myBookings);
//        }
//
//        return "dashboard";
//    }
//}

package com.example.calendit.controller;

import com.example.calendit.model.Booking;
import com.example.calendit.model.Slot;
import com.example.calendit.model.User;
import com.example.calendit.service.BookingService;
import com.example.calendit.service.SlotService;
import com.example.calendit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;


import com.example.calendit.model.Booking;
import com.example.calendit.model.User;
import com.example.calendit.service.BookingService;
import com.example.calendit.service.SlotService;
import com.example.calendit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//public class DashboardController {
//
//    private final UserService userService;
//    private final SlotService slotService;
//    private final BookingService bookingService;
//
//    @GetMapping("/dashboard")
//    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
//        String email = principal.getAttribute("email");
//        String name = principal.getAttribute("name");
//        String picture = principal.getAttribute("picture");
//
//        // Create or get user from database
//        User user = userService.saveOrUpdateUser(email, name, picture, principal.getAttribute("sub")).orElse(null);
//
//        // Set default values if user doesn't exist
//        if (user == null) {
//            user = new User();
//            user.setEmail(email);
//            user.setName(name);
//            user.setPicture(picture);
//        }
//
//        List<Slot> mySlots = new ArrayList<>();
//        List<Booking> myBookings = new ArrayList<>();
//
//        if (user.getId() != null) {
//            mySlots = slotService.getAllSlotsByOwner(user);
//            myBookings = bookingService.getBookingsByUser(user);
//        }
//
//        model.addAttribute("user", user);
//        model.addAttribute("name", name);
//        model.addAttribute("picture", picture);
//        model.addAttribute("email", email);
//        model.addAttribute("mySlots", mySlots);
//        model.addAttribute("myBookings", myBookings);
//
//        return "dashboard";
//    }
//}

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final SlotService slotService;
    private final BookingService bookingService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal Object principal, Model model) {
        String email = null;
        String name = null;
        String picture = null;
        String googleId = null;
        User user = null;

        if (principal instanceof OAuth2User oAuth2User){
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
            picture = oAuth2User.getAttribute("picture");
            googleId = oAuth2User.getAttribute("sub");

            user = userService.saveOrUpdateUser(email, name, picture, googleId).orElse(null);
        }

        else if (principal instanceof UserDetails userDetails){
            email = userDetails.getUsername();
            user = userService.findByEmail(email).orElse(null);

            if (user != null) {
                name = user.getName();
                picture = (user.getPicture() != null) ? user.getPicture() : "/images/default.png";
            } else {
                picture = "/images/default.png";
            }
        }

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPicture(picture);
        }

        List<Slot> mySlots = new ArrayList<>();
        List<Booking> myBookings = new ArrayList<>();

        if (user.getId() != null) {
            // This will load slots with booking info due to EAGER fetch
            mySlots = slotService.getAllSlotsByOwner(user);
            myBookings = bookingService.getBookingsByUser(user);
        }

        model.addAttribute("user", user);
        model.addAttribute("name", name);
        model.addAttribute("picture", picture);
        model.addAttribute("email", email);
        model.addAttribute("mySlots", mySlots);
        model.addAttribute("myBookings", myBookings);

        return "dashboard";
    }
}