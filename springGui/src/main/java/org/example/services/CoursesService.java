package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.data.CourseData;
import org.example.data.TeacherData;
import org.example.repositories.CoursesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoursesService {
    private final CoursesRepository coursesRepository;

    public List<CourseData> listCourses(String name) {
        List<CourseData> courses = coursesRepository.findAll();
        if (courses != null) {
            coursesRepository.findByName(name);
        }
        return coursesRepository.findAll();
    }

    public List<CourseData> listCoursesByUniversity(int universityId) {
        return coursesRepository.findByUniversityId(universityId);
    }

    public void saveCourse(CourseData course) {
        log.info("Saved new course " + course);
        coursesRepository.save(course);
    }

    public void deleteCourse(int id) {
        coursesRepository.deleteById(id);
    }

    public CourseData getCourseById(int id) {
        if (coursesRepository.findById(id).isPresent()) {
            return coursesRepository.findById(id).get();
        }
        throw new IllegalArgumentException("Cannot find teacher by id = " + id);
    }
}
