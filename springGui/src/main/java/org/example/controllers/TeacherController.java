package org.example.controllers;

import course.Course;
import lombok.RequiredArgsConstructor;
import org.example.CsvParser;
import org.example.data.CourseData;
import org.example.data.PlaceData;
import org.example.data.TeacherData;
import org.example.repositories.CoursesRepository;
import org.example.repositories.PlacesRepository;
import org.example.services.CoursesService;
import org.example.services.TeacherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import person.Teacher;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;
    private final PlacesRepository placeRepository;
    private final CoursesRepository courseRepository;
    private final CoursesService coursesService;


    @GetMapping("/teachers")
    public String teachers(@RequestParam(name = "name", required = false) String name, Model model) {
        model.addAttribute("teachers", teacherService.listTeachers(name));
        //model.addAttribute("places", placeRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
        return "teachers";
    }

    @GetMapping("/teacher/{id}")
    public String teacherInfo(@PathVariable int id, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(id));
        return "teacher-info";
    }

    @GetMapping("/teacher/create")
    public String showCreateTeacherForm(Model model) {
        model.addAttribute("teacher");
        model.addAttribute("places", placeRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
        return "teacher-create";

    }

    @PostMapping("/teacher/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to import.");
            return "redirect:/teachers"; // Adjust the redirection URL as needed
        }

        List<TeacherData> importedData = CsvParser.parseTeachers(file);
        importedData.forEach(teacherService::saveTeacher);

        redirectAttributes.addFlashAttribute("message", "File successfully imported.");

        return "redirect:/teachers"; // Redirect to the courses page or adjust the URL as needed
    }


    @PostMapping("/teacher/create")
    public String createTeacher(TeacherData teacher) {
        teacherService.saveTeacher(teacher);
        return "redirect:/teachers";
    }

    @PostMapping("/teacher/delete/{id}")
    public String deleteTeacher(@PathVariable int id) {
        teacherService.deleteTeacher(id);
        return "redirect:/teachers";
    }

    @GetMapping("/teacher/edit/{id}")
    public String editTeacher(@PathVariable int id, Model model) {
        TeacherData teacher = teacherService.getTeacherById(id);
        List<CourseData> courses = courseRepository.findAll();
        model.addAttribute("teacher", teacher);
        model.addAttribute("courses", courses);
        return "teacher-create";  // возвращаем шаблон teacher-create.ftlh
    }

}