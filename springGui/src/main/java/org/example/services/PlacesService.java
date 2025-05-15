package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.data.PlaceData;
import org.example.repositories.PlacesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlacesService {
    private final PlacesRepository placesRepository;

    public List<PlaceData> listPlaces(String name) {
        List<PlaceData> places = placesRepository.findAll();
        if (places != null) {
            placesRepository.findByName(name);
        }
        return placesRepository.findAll();
    }

    public void savePlace(PlaceData place) {
        log.info("Saved new teacher " + place);
        placesRepository.save(place);
    }

    public PlaceData getPlaceByName(String name) {
        if (placesRepository.findByName(name) != null) {
            return placesRepository.findByName(name).get(0);
        }
        throw new IllegalArgumentException("Cannot find place by name = " + name);
    }

    public void deletePlace(int id) {
        placesRepository.deleteById(id);
    }

    public PlaceData getPlaceById(int id) {
        if (placesRepository.findById(id).isPresent()) {
            return placesRepository.findById(id).get();
        }
        throw new IllegalArgumentException("Cannot find place by id = " + id);
    }
}
