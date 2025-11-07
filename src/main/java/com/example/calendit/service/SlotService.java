package com.example.calendit.service;

import com.example.calendit.model.Slot;
import com.example.calendit.model.User;
import com.example.calendit.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SlotService {
    
    private final SlotRepository slotRepository;
    private final EmailService emailService;
    
    @Transactional
    public void createSlot(User owner, String meetingName, int durationMinutes, LocalDate date, LocalTime time) {
        Slot slot = new Slot();
        slot.setOwner(owner);
        slot.setMeetingName(meetingName);
        slot.setDurationMinutes(durationMinutes);
        slot.setDate(date);
        slot.setTime(time);
        slot.setAvailable(true);
        slotRepository.save(slot);
    }
    
    public List<Slot> getAvailableSlots(User owner) {
        return slotRepository.findByOwnerAndDateAfterAndAvailableTrue(owner, LocalDate.now());
    }
    
    public List<Slot> getAllSlotsByOwner(User owner) {
        return slotRepository.findByOwner(owner);
    }
    
    public Optional<Slot> findById(Long id) {
        return slotRepository.findById(id);
    }

    @Transactional
    public void deleteSlot(Long slotId) {
        Optional<Slot> slotOpt = slotRepository.findById(slotId);

        if (slotOpt.isPresent()) {
            Slot slot = slotOpt.get();

            // If the slot had a booking, notify both users
            if (!slot.isAvailable() && slot.getBooking() != null) {
                User bookedUser = slot.getBooking().getBookedBy();

                String subject = "❌ Your meeting has been canceled";

                String bodyForOwner = String.format("""
                Hello %s,

                Your meeting slot on %s at %s has been canceled.

                Regards,
                Calendit Team
                """,
                        slot.getOwner().getName(),
                        slot.getDate(),
                        slot.getTime()
                );

                String bodyForBooker = String.format("""
                Hello %s,

                The meeting you booked with %s on %s at %s has been canceled by the host.

                Regards,
                Calendit Team
                """,
                        bookedUser.getName(),
                        slot.getOwner().getName(),
                        slot.getDate(),
                        slot.getTime()
                );

                emailService.sendEmail(slot.getOwner().getEmail(), subject, bodyForOwner);
                emailService.sendEmail(bookedUser.getEmail(), subject, bodyForBooker);

            } else {
                // Notify owner only (slot never booked)
                String subject = "Slot removed successfully";
                String body = String.format("""
                Hello %s,

                Your available slot on %s at %s has been successfully removed.

                Regards,
                Calendit Team
                """,
                        slot.getOwner().getName(),
                        slot.getDate(),
                        slot.getTime()
                );

                emailService.sendEmail(slot.getOwner().getEmail(), subject, body);
            }

            slotRepository.deleteById(slotId);
        }
    }

    @Transactional
    public void markSlotAsBooked(Slot slot) {
        slot.setAvailable(false);
        slotRepository.save(slot);
    }
    
    @Transactional
    public void markSlotAsAvailable(Slot slot) {
        slot.setAvailable(true);
        slotRepository.save(slot);
    }
    public List<Slot> getUpcomingSlots(User owner) {
        return slotRepository.findByOwnerAndDateGreaterThanEqual(owner, LocalDate.now());
    }

    public List<Slot> getPastSlots(User owner) {
        return slotRepository.findByOwnerAndDateLessThan(owner, LocalDate.now());
    }


}
