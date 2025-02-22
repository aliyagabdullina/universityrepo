package person;

public class StudentImpl implements Student {
    private final String _name;

    public StudentImpl(String name) {
        _name = name;
    }

    @Override
    public String getName() {
        return _name;
    }
}
