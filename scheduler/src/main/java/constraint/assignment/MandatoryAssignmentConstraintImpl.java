package constraint.assignment;

import lesson.Lesson;

import java.util.List;
import java.util.stream.Stream;

public class MandatoryAssignmentConstraintImpl implements MandatoryAssignmentConstraint {
    private final List<Object> _list;

    public MandatoryAssignmentConstraintImpl(List<Object> list) {
        _list = list;
    }

    @Override
    public Stream<Object> getObjects() {
        return _list.stream();
    }

    @Override
    public boolean ifConsistent(Stream<Lesson> schedule) {
        return true;
    }
}
