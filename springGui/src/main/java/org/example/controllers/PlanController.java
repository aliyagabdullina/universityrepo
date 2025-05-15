package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.CsvParser;
import org.example.data.CourseData;
import org.example.data.StudentData;
import org.example.data.TeacherData;
import org.example.repositories.CoursesRepository;
import org.example.repositories.PlacesRepository;
import org.example.repositories.StudentRepository;
import org.example.repositories.TeacherRepository;
import org.example.services.CoursesService;
import org.example.services.StudentService;
import org.example.services.TeacherService;
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
public class PlanController {
    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final CoursesRepository courseRepository;
    private final CoursesService coursesService;

    int universityId = 1;


    @GetMapping("/plan")
    public String teachers(@RequestParam(name = "name", required = false) String name, Model model) {
        model.addAttribute("students", studentService.listStudents(name));
        return "plans";
    }

    @GetMapping("/plan/create")
    public String showCreatePlanForm(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
        return "plan-create";

    }

    @PostMapping("/plan/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to import.");
            return "redirect:/plans"; // Adjust the redirection URL as needed
        }
        //TODO
        return "redirect:/plans"; // Redirect to the courses page or adjust the URL as needed
    }


    @PostMapping("/plan/create")
    public String createPlan(StudentData student) {
        studentService.saveStudent(student);
        return "redirect:/plans";
    }

    @PostMapping("/plan/delete/{id}")
    public String deletePlan(@PathVariable int id) {
        return "redirect:/plans";
    }

    @GetMapping("/plan/edit/{id}")
    public String editPlan(@PathVariable int id, Model model) {
        List<CourseData> courses = coursesService.listCoursesByUniversity(universityId);
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("courses", courses);
        return "plan-create";  // возвращаем шаблон teacher-create.ftlh

    }
}
