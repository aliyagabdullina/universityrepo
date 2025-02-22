package org.example.controllers;


import lombok.RequiredArgsConstructor;
import org.example.repositories.LessonRepository;
import org.example.services.TimeSlotService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TimeSlotController {
    private final TimeSlotService timeslotService;
    private final LessonRepository lessonsRepository;

    @GetMapping("/timeslot")
    public String timeslot(@RequestParam(name = "timeslot_id", required = false) Integer timeslot_id, Model model) {
        int id = 0;
        if (timeslot_id != null) {
             id = timeslot_id.intValue();
        }
        model.addAttribute("timeslots", timeslotService.listTimeSlots(id));
        //model.addAttribute("lessons", lessonsRepository.findAll());
        return "timeslot";
    }

    @GetMapping("/timeslot-create")
    public String timeslotCreate() {
        return "timeslot-create";
    }
}
