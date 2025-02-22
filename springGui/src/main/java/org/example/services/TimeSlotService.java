package org.example.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.data.TimeSlotData;
import org.example.repositories.TimeSlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeSlotService {
    private final TimeSlotRepository timeSlotRepository;

    public List<TimeSlotData> listTimeSlots(int id) {
        return timeSlotRepository.findById(id).stream().toList();
    }

    public void saveTimeSlot(TimeSlotData timeSlot) {
        log.info("Saved new time slot " + timeSlot);
        timeSlotRepository.save(timeSlot);
    }

    public void deleteTimeSlot(int id) {
        timeSlotRepository.deleteById(id);
    }

    public TimeSlotData getTimeSlotById(int id) {
        Optional<TimeSlotData> optionalTimeSlot = timeSlotRepository.findById(id);
        if (optionalTimeSlot.isPresent()) {
            return optionalTimeSlot.get();
        }
        throw new IllegalArgumentException("Cannot find time slot by id = " + id);
    }

}
