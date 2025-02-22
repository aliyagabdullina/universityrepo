package group;

import person.Student;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class GroupImpl implements Group {
    private final String _name;
    private final Set<Student> _students;


    public GroupImpl(String name, Set<Student> students) {
        _name = name;
        _students = students;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Stream<Student> getStudents() {
        return _students.stream();
    }

    @Override
    public int size() {
        return _students.size();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupImpl group = (GroupImpl) o;
        return Objects.equals(_name, group._name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_name);
    }

    @Override
    public String toString() {
        return _name;
    }
}
