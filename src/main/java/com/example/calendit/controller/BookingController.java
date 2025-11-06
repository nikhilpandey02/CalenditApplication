package com.example.calendit.controller;

import com.example.calendit.model.Booking;
import com.example.calendit.model.Slot;
import com.example.calendit.model.User;
import com.example.calendit.service.*;
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
    private final EmailService emailService;

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

            if (slot.getOwner().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("failure", "You cannot book your own slot");
                return "redirect:/dashboard";
            }

            // Only create event after we know booking is valid
            String accessToken = authorizedClient.getAccessToken().getTokenValue();
            String googleEventId = googleCalendarService.createCalendarEvent(
                    accessToken,
                    slot.getOwner().getEmail(),
                    user.getEmail(),
                    slot.getDate(),
                    slot.getTime(),
                    "Meeting with " + slot.getOwner().getName()
            );

            boolean isBooked = bookingService.createBooking(slot, user, googleEventId);

            if (isBooked) {
                redirectAttributes.addFlashAttribute("success", "Booking created successfully!");

                // Send confirmation emails
                String subject = "📅 Your meeting is booked!";
                String body = String.format("""
                Hello %s,

                Your meeting has been scheduled on %s at %s.
                Join via Google Meet: %s

                Regards,
                Calendit Team
                """, user.getName(), slot.getDate(), slot.getTime(), googleEventId);

                emailService.sendBookingConfirmation(user.getEmail(), subject, body);
                emailService.sendBookingConfirmation(slot.getOwner().getEmail(), subject, body);
            } else {
                redirectAttributes.addFlashAttribute("failure", "You cannot book your own slot");
            }
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
