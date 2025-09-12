package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.data.TeacherData;
import org.example.repositories.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public List<TeacherData> listTeachersByUniversity(int universityId) {
        return teacherRepository.findByUniversityId(universityId);
    }


    public List<TeacherData> listTeachers(String name) {
        List<TeacherData> teachers = teacherRepository.findAll();
        if (teachers != null) {
            teacherRepository.findByName(name);
        }
        return teacherRepository.findAll();
    }

    public void saveTeacher(TeacherData teacher) {
        log.info("Saved new teacher " + teacher);
        teacherRepository.save(teacher);
    }

    public void deleteTeacher(int id) {
        teacherRepository.deleteById(id);
    }

    public TeacherData getTeacherById(int id) {
        if (teacherRepository.findById(id).isPresent()) {
            return teacherRepository.findById(id).get();
        }
        throw new IllegalArgumentException("Cannot find teacher by id = " + id);
    }

}
