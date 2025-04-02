package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.data.StudentData;
import org.example.data.TeacherData;
import org.example.repositories.StudentRepository;
import org.example.repositories.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    public List<StudentData> listStudents(String name) {
        List<StudentData> students = studentRepository.findAll();
        if (students != null) {
            studentRepository.findByName(name);
        }
        return studentRepository.findAll();
    }

    public void saveStudent(StudentData student) {
        log.info("Saved new student " + student);
        studentRepository.save(student);
    }

    public void deleteStudent(int id) {
        studentRepository.deleteById(id);
    }

    public StudentData getStudentById(int id) {
        if (studentRepository.findById(id).isPresent()) {
            return studentRepository.findById(id).get();
        }
        throw new IllegalArgumentException("Cannot find teacher by id = " + id);
    }

}
