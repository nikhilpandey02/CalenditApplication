package com.example.calendit.repository;

import com.example.calendit.model.Slot;
import com.example.calendit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByOwnerAndAvailableTrue(User owner);
    List<Slot> findByOwnerAndDateAfterAndAvailableTrue(User owner, LocalDate date);
    List<Slot> findByOwner(User owner);
}
