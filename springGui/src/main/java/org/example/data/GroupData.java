package org.example.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Groups")
@Data
@NoArgsConstructor
public class GroupData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "groupId")
    private int groupId;
    @Column(name = "name")
    private String name;
    @Column(name = "num_of_students")
    private int numOfStudents;
    @Column(name = "max_number_of_lessons")
    private int maxNumberOfLessons;
    @Column(name = "university_id")
    private Integer universityId;

    @ManyToMany(mappedBy = "groups")
    private List<ProgramData> programs;
}
