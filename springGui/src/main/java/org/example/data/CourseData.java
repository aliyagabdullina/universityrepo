package org.example.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "Courses")
@Data
@NoArgsConstructor
public class CourseData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "course_id")
    private int courseId;
    @Column(name = "name")
    private String name;
    @Column(name = "num_of_lessons_per_week")
    private int numOfLessonsPerWeek;
    @Column(name = "num_of_days_per_week")
    private int numOfDaysPerWeek;
    @Column(name = "max_lessons_per_day")
    private int maxLessonsPerDay;
    @Column(name = "complexity")
    private String complexity;

}