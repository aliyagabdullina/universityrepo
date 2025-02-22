package place;

public interface Building {
    Address getAddress();
    String getName();

    static Building createEmpty() {
        return new BuildingImpl("Empty", Address.createEmpty());
    }
}
