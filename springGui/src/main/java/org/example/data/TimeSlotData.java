package org.example.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Timeslot")
@Data
@NoArgsConstructor
public class TimeSlotData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "timeslot_id")
    private int timeSlotId;
    @Column(name = "lesson_id")
    private int lessonId;

}