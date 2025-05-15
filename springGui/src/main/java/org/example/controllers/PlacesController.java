package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.CsvParser;
import org.example.data.PlaceData;
import org.example.services.PlacesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import place.Place;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PlacesController {
    private final PlacesService placeService;

    @GetMapping("/places")
    public String places(@RequestParam(name = "name", required = false) String name, Model model) {
        model.addAttribute("places", placeService.listPlaces(name));
        return "places";
    }
    @PostMapping("/places")
    public String placesOpen() {
        return "redirect:/places";
    }

    @GetMapping("/place/{id}")
    public String placeInfo(@PathVariable int id, Model model) {
        model.addAttribute("place", placeService.getPlaceById(id));
        return "place-info";
    }

    @GetMapping("/place/create")
    public String showCreatePlaceForm(Model model) {
        model.addAttribute("place");
        return "place-create";
    }

    @PostMapping("/place/create")
    public String createPlace(PlaceData place) {
        placeService.savePlace(place);
        return "redirect:/places";
    }

    @PostMapping("/place/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to import.");
            return "redirect:/places"; // Adjust the redirection URL as needed
        }

        List<PlaceData> importedData = CsvParser.parsePlaces(file);
        importedData.forEach(placeService::savePlace);

        redirectAttributes.addFlashAttribute("message", "File successfully imported.");

        return "redirect:/places"; // Redirect to the courses page or adjust the URL as needed
    }


    @PostMapping("/place/delete/{id}")
    public String deletePlace(@PathVariable int id) {
        placeService.deletePlace(id);
        return "redirect:/places";
    }
    @GetMapping("/place/edit/{id}")
    public String editPlace(@PathVariable int id, Model model) {
        PlaceData place = placeService.getPlaceById(id);
        model.addAttribute("place", place);
        return "place-create";  // возвращаем шаблон place-create.ftlh
    }

}
