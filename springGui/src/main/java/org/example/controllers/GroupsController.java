package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.CsvParser;
import org.example.data.GroupData;
import org.example.data.PlaceData;
import org.example.repositories.PlacesRepository;
import org.example.repositories.TeacherRepository;
import org.example.services.GroupsService;
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
public class GroupsController {
    private final GroupsService groupsService;
    private final PlacesRepository placeRepository;
    private final TeacherRepository teacherRepository;
    private int universityId= 1;

    @GetMapping("/groups")
    public String groups(@RequestParam(name = "name", required = false) String name, Model model) {
        model.addAttribute("groups", groupsService.listGroupsByUniversity(universityId));
        model.addAttribute("places", placeRepository.findAll());
        model.addAttribute("teachers", teacherRepository.findAll());
        return "groups";
    }

    @PostMapping("/groups/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to import.");
            return "redirect:/groups"; // Adjust the redirection URL as needed
        }

        List<GroupData> importedData = CsvParser.parseGroups(file);
        importedData.forEach(groupsService::saveGroup);

        redirectAttributes.addFlashAttribute("message", "File successfully imported.");

        return "redirect:/groups"; // Redirect to the courses page or adjust the URL as needed
    }


    @GetMapping("/group/{id}")
    public String groupInfo(@PathVariable int id, Model model) {
        model.addAttribute("group", groupsService.getGroupById(id));
        return "group-info";
    }

    @GetMapping("/group/create")
    public String showCreateGroupForm(Model model) {
        model.addAttribute("group");
        model.addAttribute("places", placeRepository.findAll());
        model.addAttribute("teachers", teacherRepository.findAll());
        return "group-create";
    }

    @PostMapping("/group/create")
    public String createGroup(GroupData group) {
        groupsService.saveGroup(group);
        return "redirect:/groups";
    }

    @PostMapping("/group/delete/{id}")
    public String deleteGroup(@PathVariable int id) {
        groupsService.deleteGroup(id);
        return "redirect:/groups";
    }
    @GetMapping("/group/edit/{id}")
    public String editGroup(@PathVariable int id, Model model) {
        GroupData group = groupsService.getGroupById(id);
        model.addAttribute("group", group);
        return "group-create";  // возвращаем шаблон group-create.ftlh
    }
}
