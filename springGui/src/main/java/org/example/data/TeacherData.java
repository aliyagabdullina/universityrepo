package org.example.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Teachers")
@Data
@NoArgsConstructor
public class TeacherData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "teacher_id")
    private int teacherId;
    @Column(name = "name")
    private String name;
    @Column(name = "place_id")
    private int placeId;
    @Column(name = "max_number_of_lessons")
    private int maxNumberOfLessons;
    @Column(name = "timeslot_id")
    private int timeslotId;


    @Column(name = "university_id")
    private int universityId;

    @ManyToMany
    @JoinTable(
            name = "courseteacher", // имя связующей таблицы
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<CourseData> courses;


}
