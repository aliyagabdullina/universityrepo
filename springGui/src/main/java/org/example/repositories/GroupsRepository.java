package org.example.repositories;

import org.example.data.GroupData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupsRepository extends JpaRepository<GroupData, Integer> {

    List<GroupData> findByName(String name);
    List<GroupData> findByUniversityId(Integer universityId);

}
