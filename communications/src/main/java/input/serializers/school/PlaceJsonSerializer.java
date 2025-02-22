package input.serializers.school;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import input.serializers.VocabularySchoolJsonProperties;
import place.Place;

import java.lang.reflect.Type;

public class PlaceJsonSerializer implements JsonSerializer<Place> {
    private final VocabularySchoolJsonProperties _vocabulary;

    public PlaceJsonSerializer(VocabularySchoolJsonProperties vocabulary) {
        _vocabulary = vocabulary;
    }

    @Override
    public JsonElement serialize(Place place, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        String nameLabel = _vocabulary.getLabel_objName();
        result.addProperty(nameLabel, place.getName());
        String buildingLabel = _vocabulary.getLabel_building();

        result.addProperty(buildingLabel, place.getBuilding().getName());
        return result;
    }
}
