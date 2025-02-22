package org.example.repositories;

import org.example.data.CourseData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursesRepository extends JpaRepository<CourseData, Integer> {

    List<CourseData> findByName(String name);
}
