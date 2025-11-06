package com.example.calendit.service;

import com.example.calendit.model.Booking;
import com.example.calendit.model.Slot;
import com.example.calendit.model.User;
import com.example.calendit.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final SlotService slotService;
    
    @Transactional
    public boolean createBooking(Slot slot, User bookedBy, String googleEventId) {
        if(slot.getOwner().getId().equals(bookedBy.getId())){
            return false;
        }
        Booking booking = new Booking();
        booking.setSlot(slot);
        booking.setBookedBy(bookedBy);
        booking.setBookingTime(LocalDateTime.now());
        booking.setGoogleEventId(googleEventId);
        
        slotService.markSlotAsBooked(slot);
        
        bookingRepository.save(booking);

        return true;
    }

    
    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByBookedBy(user);
    }
    
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }
    
    @Transactional
    public void deleteBooking(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            Slot slot = booking.get().getSlot();
            slotService.markSlotAsAvailable(slot);
            bookingRepository.deleteById(bookingId);
        }
    }
}
