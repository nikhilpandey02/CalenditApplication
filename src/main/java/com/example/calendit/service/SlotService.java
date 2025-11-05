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
        slotRepository.deleteById(slotId);
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
}
