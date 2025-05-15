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
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "max_number_of_lessons")
    private Integer maxNumberOfLessons;
    @Column(name = "degree")
    private String degree;

    @Column(name = "university_id")
    private int universityId;

    @ManyToMany
    @JoinTable(
            name = "courseteacher", // имя связующей таблицы
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<CourseData> courses;

    @ManyToMany(mappedBy = "teachers")
    private List<GroupData> groups;

    @Override
    public String toString() {
        return "TeacherData{id=" + teacherId + ", name=" + name + ", email=" + email + "}";
    }



}
