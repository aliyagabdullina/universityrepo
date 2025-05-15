package org.example.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

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

}
