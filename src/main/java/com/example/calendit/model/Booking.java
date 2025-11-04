package com.example.calendit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    // Change to EAGER so bookedBy user is loaded with booking
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booked_by_id", nullable = false)
    private User bookedBy;

    @Column(name = "booking_time")
    private LocalDateTime bookingTime;

    @Column(name = "google_event_id")
    private String googleEventId;
}
