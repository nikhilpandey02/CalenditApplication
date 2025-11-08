package com.example.calendit.repository;

import com.example.calendit.model.Slot;
import com.example.calendit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByOwner(User owner);
    List<Slot> findByOwnerAndAvailableTrue(User owner);
    List<Slot> findByOwnerAndDateGreaterThanEqualAndAvailableTrue(User owner, LocalDate date);
    List<Slot> findByOwnerAndDateGreaterThanEqual(User owner, LocalDate date);
    List<Slot> findByOwnerAndDateLessThan(User owner, LocalDate date);
}
