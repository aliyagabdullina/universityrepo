package org.example.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
}
