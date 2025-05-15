package org.example.repositories;

import org.example.data.StudentData;
import org.example.data.TeacherData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<StudentData, Integer> {

    List<StudentData> findByName(String name);
    List<StudentData> findByUniversityId(Integer universityId);


}
