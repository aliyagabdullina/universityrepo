package course;

import java.util.Objects;

public class CourseImpl implements Course {
    private final String _name;
    private String _aliasing;

    public CourseImpl(String name) {
        _name = name;
        _aliasing = name;
    }
    @Override
    public void setAliasing(String aliasing) {
        _aliasing = aliasing;
    }

    @Override
    public String getAliasing() {
        return _aliasing;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return _name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseImpl course = (CourseImpl) o;
        return Objects.equals(_name, course._name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_name);
    }
}
