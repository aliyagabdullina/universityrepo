package org.example.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Id;
import javax.persistence.*;

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


}
