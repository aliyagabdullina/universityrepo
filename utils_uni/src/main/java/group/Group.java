package group;

import person.Student;

import java.util.stream.Stream;

public interface Group {
    String getName();
    Stream<Student> getStudents();
    int size();
}
