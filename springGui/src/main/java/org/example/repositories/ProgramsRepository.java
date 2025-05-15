package org.example.repositories;

import org.example.data.ProgramData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramsRepository extends JpaRepository<ProgramData, Integer> {

    List<ProgramData> findByName(String name);
    List<ProgramData> findByUniversityId(Integer universityId);


}
