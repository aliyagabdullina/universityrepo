package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.data.GroupData;
import org.example.repositories.GroupsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupsService {
    private final GroupsRepository groupsRepository;

    public List<GroupData> listGroups(String name) {
        List<GroupData> groups = groupsRepository.findAll();
        if (groups != null) {
            groupsRepository.findByName(name);
        }
        return groupsRepository.findAll();
    }

    public void saveGroup(GroupData group) {
        log.info("Saved new group " + group);
        groupsRepository.save(group);
    }

    public void deleteGroup(int id) {
        groupsRepository.deleteById(id);
    }

    public GroupData getGroupById(int id) {
        if (groupsRepository.findById(id).isPresent()) {
            return groupsRepository.findById(id).get();
        }
        throw new IllegalArgumentException("Cannot find group by id = " + id);
    }
}
