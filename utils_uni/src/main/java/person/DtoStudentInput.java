package person;

public class DtoStudentInput {
    public String name;

    public DtoStudentInput(Student student) {
        name = student.getName();
    }

    public DtoStudentInput() {

    }
}
