package group;

import person.Student;

import java.util.ArrayList;
import java.util.List;

public class DtoGroupInput {
    public String name;
    public List<String> tags = new ArrayList<>();
    public List<String> students = new ArrayList<>();

    public DtoGroupInput(Group group) {
        name = group.getName();
        students = group.getStudents()
                .map(Student::getName)
                .toList();
    }

    public DtoGroupInput() {
    }
}
