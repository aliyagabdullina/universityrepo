package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.data.ProgramData;
import org.example.data.TeacherData;
import org.example.repositories.ProgramsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgramsService {
    private final ProgramsRepository programsRepository;

    public List<ProgramData> listPrograms(String name) {
        List<ProgramData> programs = programsRepository.findAll();
        if (programs != null) {
            programsRepository.findByName(name);
        }
        return programsRepository.findAll();
    }

    public List<ProgramData> listProgramsByUniversity(int universityId) {
        return programsRepository.findByUniversityId(universityId);
    }

    public void saveProgram(ProgramData program) {
        log.info("Saved new program " + program);
        programsRepository.save(program);
    }

    public void deleteProgram(int id) {
        programsRepository.deleteById(id);
    }

    public ProgramData getProgramById(int id) {
        if (programsRepository.findById(id).isPresent()) {
            return programsRepository.findById(id).get();
        }
        throw new IllegalArgumentException("Cannot find program by id = " + id);
    }
}
