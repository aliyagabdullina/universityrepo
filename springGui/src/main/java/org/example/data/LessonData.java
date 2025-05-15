package org.example.data;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "LessonsTime")
@Data
@NoArgsConstructor
public class LessonData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "lesson_id")
    private int lessonId;
    @Column(name = "startTime")
    private Time startTime;

    @Column(name = "endTime")
    private Time endTime;


}
