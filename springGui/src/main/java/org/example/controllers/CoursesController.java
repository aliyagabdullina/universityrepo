package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.CsvParser;
import org.example.data.CourseData;
import org.example.repositories.PlacesRepository;
import org.example.services.CoursesService;
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
public class CoursesController {
    private final CoursesService coursesService;
    private final PlacesRepository placeRepository;

    private int universityId= 1;
    @PostMapping("/course/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to import.");
            return "redirect:/courses"; // Adjust the redirection URL as needed
        }

        List<CourseData> importedData = CsvParser.parseCourses(file);
        importedData.forEach(coursesService::saveCourse);

        redirectAttributes.addFlashAttribute("message", "File successfully imported.");

        return "redirect:/courses"; // Redirect to the courses page or adjust the URL as needed
    }

    @GetMapping("/courses")
    public String courses(@RequestParam(name = "name", required = false) String name, Model model) {
        model.addAttribute("courses", coursesService.listCoursesByUniversity(universityId));
        model.addAttribute("sender", "You");
        model.addAttribute("message", "Hello!\n");
        model.addAttribute("aiSender", "AI");
        model.addAttribute("aiMessage", "How can I assist you?\n");
        model.addAttribute("sender2", "You");
        model.addAttribute("message2", "Add new Teacher - Valentina\n");
        model.addAttribute("aiSender2", "AI");
        model.addAttribute("aiMessage2", "Done!\n");
        model.addAttribute("places", placeRepository.findAll());
        return "courses";
    }

    @GetMapping("/course/{id}")
    public String courseInfo(@PathVariable int id, Model model) {
        model.addAttribute("course", coursesService.getCourseById(id));
        return "course-info";
    }

    @GetMapping("/course/create")
    public String showCreateCourseForm(Model model) {
        model.addAttribute("course");
        model.addAttribute("places", placeRepository.findAll());
        return "course-create";
    }

    @PostMapping("/course/create")
    public String createCourse(CourseData course) {
        coursesService.saveCourse(course);
        return "redirect:/courses";
    }

    @PostMapping("/course/delete/{id}")
    public String deleteCourse(@PathVariable int id) {
        coursesService.deleteCourse(id);
        return "redirect:/courses";
    }
    @GetMapping("/course/edit/{id}")
    public String editCourse(@PathVariable int id, Model model) {

            CourseData course = coursesService.getCourseById(id);
            model.addAttribute("course", course);
        return "course-create";
    }

}
