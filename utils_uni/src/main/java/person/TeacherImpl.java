package person;

import java.util.Objects;

public class TeacherImpl implements Teacher {
    private final String _name;

    public TeacherImpl(String name) {
        _name = name;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeacherImpl teacher = (TeacherImpl) o;
        return Objects.equals(_name, teacher._name);
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
