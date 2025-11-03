package com.example.calendit.controller;

import com.example.calendit.model.Booking;
import com.example.calendit.model.Slot;
import com.example.calendit.model.User;
import com.example.calendit.service.BookingService;
import com.example.calendit.service.SlotService;
import com.example.calendit.service.UserService;
import com.example.calendit.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;
    private final SlotService slotService;
    private final UserService userService;
    private final GoogleCalendarService googleCalendarService;
    
    @PostMapping("/book")
    public String bookSlot(@AuthenticationPrincipal OAuth2User principal,
                           @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                           @RequestParam Long slotId,
                           RedirectAttributes redirectAttributes) {
        String email = principal.getAttribute("email");
        Optional<User> userOpt = userService.findByEmail(email);
        Optional<Slot> slotOpt = slotService.findById(slotId);
        
        if (userOpt.isPresent() && slotOpt.isPresent()) {
            Slot slot = slotOpt.get();
            User user = userOpt.get();
            
            if (!slot.isAvailable()) {
                redirectAttributes.addFlashAttribute("error", "Slot is no longer available!");
                return "redirect:/dashboard";
            }
            
            String accessToken = authorizedClient.getAccessToken().getTokenValue();
            String googleEventId = googleCalendarService.createCalendarEvent(
                    accessToken,
                    slot.getOwner().getEmail(),
                    user.getEmail(),
                    slot.getDate(),
                    slot.getTime(),
                    "Meeting with " + slot.getOwner().getName()
            );
            
            bookingService.createBooking(slot, user, googleEventId);
            redirectAttributes.addFlashAttribute("success", "Booking created successfully!");
        }
        
        return "redirect:/dashboard";
    }
    
    @PostMapping("/cancel")
    public String cancelBooking(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
                                @RequestParam Long bookingId,
                                RedirectAttributes redirectAttributes) {
        Optional<Booking> bookingOpt = bookingService.findById(bookingId);
        
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            String accessToken = authorizedClient.getAccessToken().getTokenValue();
            
            if (booking.getGoogleEventId() != null) {
                googleCalendarService.deleteCalendarEvent(accessToken, booking.getGoogleEventId());
            }
            
            bookingService.deleteBooking(bookingId);
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully!");
        }
        
        return "redirect:/dashboard";
    }
}
