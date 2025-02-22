package org.example.repositories;

import org.example.data.TeacherData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherRepository extends JpaRepository<TeacherData, Integer> {

    List<TeacherData> findByName(String name);

}
