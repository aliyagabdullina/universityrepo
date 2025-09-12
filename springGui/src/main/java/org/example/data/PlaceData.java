package org.example.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Entity
@Table(name = "Places")
@Data
@NoArgsConstructor
public class PlaceData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "from_zero_numeration")
    @SequenceGenerator(name = "from_zero_numeration", sequenceName = "from_zero_numeration", allocationSize = 1)
    @Column(name = "place_id")
    private int placeId;
    @Column(name = "name")
    private String name;
    @Column(name = "capacity")
    private int capacity;
    @Column(name = "description")
    private String description;

    @Column(name = "university_id")
    private Integer universityId;

}
