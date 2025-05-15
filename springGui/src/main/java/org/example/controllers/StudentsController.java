package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.CsvParser;
import org.example.data.StudentData;
import org.example.data.TeacherData;
import org.example.repositories.CoursesRepository;
import org.example.repositories.PlacesRepository;
import org.example.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StudentsController {
    private final StudentService studentService;
    private final PlacesRepository placeRepository;
    private final CoursesRepository courseRepository;
    private final ProgramsService programsService;
    private final GroupsService groupsService;
    private final CoursesService courseService;


    @GetMapping("/students")
    public String students(@RequestParam(name = "name", required = false) String name, Model model) {
        model.addAttribute("students", studentService.listStudents(name));
        model.addAttribute("subjects", courseRepository.findAll());
        return "students";
    }

    @GetMapping("/student/{id}")
    public String studentInfo(@PathVariable int id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "student-info";
    }

    @GetMapping("/student/create")
    public String showCreateStudentForm(Model model) {
        model.addAttribute("student");
        model.addAttribute("places", placeRepository.findAll());
        model.addAttribute("subjects", courseRepository.findAll());
        return "student-create";

    }


    /*
    @PostMapping("/student/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to import.");
            return "redirect:/students"; // Adjust the redirection URL as needed
        }

        List<StudentData> importedData = CsvParser.parseStudents(file);
        importedData.forEach(studentService::saveStudent);

        redirectAttributes.addFlashAttribute("message", "File successfully imported.");

        return "redirect:/students"; // Redirect to the courses page or adjust the URL as needed
    }*/


    @PostMapping("/student/create")
    public String createStudent(StudentData student) {
        studentService.saveStudent(student);
        return "redirect:/students";
    }

    @PostMapping("/student/delete/{id}")
    public String deleteStudent(@PathVariable int id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }

    @GetMapping("/student/edit/{id}")
    public String editStudent(@PathVariable int id, Model model) {
        StudentData student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        return "student-create";  // возвращаем шаблон student-create.ftlh
    }
}
