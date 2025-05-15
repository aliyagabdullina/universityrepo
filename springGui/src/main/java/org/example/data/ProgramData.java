package org.example.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Programs")
@Data
@NoArgsConstructor
public class ProgramData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "program_id")
    private int programId;
    @Column(name = "name")
    private String name;
    @Column(name = "hours")
    private int hours;
    @Column(name = "complexity")
    private int complexity;

    @Column(name = "university_id")
    private Integer universityId;

    @ManyToMany
    @JoinTable(
            name = "courseprogram", // имя связующей таблицы
            joinColumns = @JoinColumn(name = "program_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<CourseData> courses;

    @ManyToMany
    @JoinTable(
            name = "groupprogram", // имя связующей таблицы
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "program_id")
    )
    private List<GroupData> groups;
}
