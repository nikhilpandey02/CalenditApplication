package com.example.calendit.repository;

import com.example.calendit.model.AvailabilitySchedule;
import com.example.calendit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface AvailabilityScheduleRepository extends JpaRepository<AvailabilitySchedule, Long> {
    List<AvailabilitySchedule> findByOwner(User owner);
    List<AvailabilitySchedule> findByOwnerAndEnabledTrue(User owner);
    List<AvailabilitySchedule> findByOwnerAndDayOfWeek(User owner, DayOfWeek dayOfWeek);
}
