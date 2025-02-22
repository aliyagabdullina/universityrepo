package org.example.repositories;

import org.example.data.PlaceData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlacesRepository  extends JpaRepository<PlaceData, Integer> {

    List<PlaceData> findByName(String name);
}
