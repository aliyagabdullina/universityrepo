package person;

import java.util.ArrayList;
import java.util.List;


public class DtoTeacherInput {
    public String name;
    public List<String> tags = new ArrayList<>();

    public DtoTeacherInput() {
    }

    public DtoTeacherInput(Teacher teacher) {
        name = teacher.getName();
    }
}
