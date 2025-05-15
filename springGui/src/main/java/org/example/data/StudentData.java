package org.example.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Students")
@Data
@NoArgsConstructor
public class StudentData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "student_id")
    private int studentId;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "group_id")
    private int groupId;
    @Column(name = "degree")
    private String degree;

    @Column(name = "university_id")
    private Integer universityId;

    @ManyToMany
    @JoinTable(
            name = "coursestudent", // имя связующей таблицы
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<CourseData> courses;

    @Override
    public String toString() {
        return "StudentData{id=" + studentId + ", name=" + name + "}";
    }

}
