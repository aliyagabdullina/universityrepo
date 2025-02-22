package org.example.repositories;

import org.example.data.TimeSlotData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSlotRepository extends JpaRepository<TimeSlotData, Integer> {
}
