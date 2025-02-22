package org.example.repositories;

import org.example.data.LessonData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<LessonData, Integer> {
}
