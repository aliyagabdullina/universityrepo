package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.CsvParser;
import org.example.data.ProgramData;
import org.example.repositories.CoursesRepository;
import org.example.repositories.GroupsRepository;
import org.example.services.ProgramsService;
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
public class ProgramsController {
    private final ProgramsService programsService;
    private final CoursesRepository courseRepository;
    private final GroupsRepository groupsRepository;
    private int universityId = 1;

    @GetMapping("/programs")
    public String programs(@RequestParam(name = "name", required = false) String name, Model model) {
        model.addAttribute("programs", programsService.listProgramsByUniversity(universityId));
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("groups", groupsRepository.findAll());
        return "programs";
    }

    @GetMapping("/program/{id}")
    public String programInfo(@PathVariable int id, Model model) {
        model.addAttribute("program", programsService.getProgramById(id));
        return "program-info";
    }

    @GetMapping("/program/create")
    public String showCreateProgramForm(Model model) {
        model.addAttribute("program");
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("groups", groupsRepository.findAll());
        return "program-create";
    }

    @GetMapping("/program/create-2")
    public String showCreateProgramForm2(Model model) {
        model.addAttribute("program");
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("groups", groupsRepository.findAll());
        return "program-create-2";
    }
    @PostMapping("/program/create-2")
    public String createProgram2(ProgramData program) {

        return "redirect:/schedule";
    }
    @PostMapping("/program/create")
    public String createProgram(ProgramData program) {
        programsService.saveProgram(program);
        return "redirect:/program/create-2";
    }

    @PostMapping("/program/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to import.");
            return "redirect:/programs"; // Adjust the redirection URL as needed
        }

        List<ProgramData> importedData = CsvParser.parsePrograms(file);
        importedData.forEach(programsService::saveProgram);

        redirectAttributes.addFlashAttribute("message", "File successfully imported.");

        return "redirect:/programs"; // Redirect to the courses page or adjust the URL as needed
    }


    @PostMapping("/program/delete/{id}")
    public String deleteProgram(@PathVariable int id) {
        programsService.deleteProgram(id);
        return "redirect:/programs";
    }
}
