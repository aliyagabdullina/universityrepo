package place;


import java.util.stream.Stream;

public interface Place {
    Building getBuilding();
    String getName();
    Stream<String> getTags();
    void addTag(String tag);
    boolean hasTag(String tag);
}
