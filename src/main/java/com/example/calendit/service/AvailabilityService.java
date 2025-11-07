package com.example.calendit.service;

import com.example.calendit.model.AvailabilitySchedule;
import com.example.calendit.model.User;
import com.example.calendit.repository.AvailabilityScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
    
    private final AvailabilityScheduleRepository availabilityRepository;
    
    @Transactional
    public AvailabilitySchedule createAvailability(User owner, DayOfWeek dayOfWeek, 
                                                   LocalTime startTime, LocalTime endTime) {
        AvailabilitySchedule schedule = new AvailabilitySchedule();
        schedule.setOwner(owner);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setEnabled(true);
        
        return availabilityRepository.save(schedule);
    }
    
    public List<AvailabilitySchedule> getAvailabilityByOwner(User owner) {
        return availabilityRepository.findByOwner(owner);
    }
    
    public Optional<AvailabilitySchedule> findById(Long id) {
        return availabilityRepository.findById(id);
    }
    
    @Transactional
    public void deleteAvailability(Long id) {
        availabilityRepository.deleteById(id);
    }
    
    @Transactional
    public void toggleAvailability(Long id) {
        Optional<AvailabilitySchedule> scheduleOpt = availabilityRepository.findById(id);
        if (scheduleOpt.isPresent()) {
            AvailabilitySchedule schedule = scheduleOpt.get();
            schedule.setEnabled(!schedule.isEnabled());
            availabilityRepository.save(schedule);
        }
    }
}
