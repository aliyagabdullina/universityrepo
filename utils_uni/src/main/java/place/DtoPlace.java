package place;


import java.util.ArrayList;
import java.util.List;

public class DtoPlace {
    public String name;
    public List<String> tagList = new ArrayList<>();

    public DtoPlace() {
    }

    public DtoPlace(Place place) {
        name = place.getName();
    }
}
