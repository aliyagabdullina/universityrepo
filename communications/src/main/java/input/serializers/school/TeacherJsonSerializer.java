package input.serializers.school;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import input.serializers.VocabularySchoolJsonProperties;
import person.Teacher;

import java.lang.reflect.Type;

public class TeacherJsonSerializer implements JsonSerializer<Teacher> {
    private final VocabularySchoolJsonProperties _vocabulary;

    public TeacherJsonSerializer(VocabularySchoolJsonProperties vocabulary) {
        _vocabulary = vocabulary;
    }

    @Override
    public JsonElement serialize(Teacher teacher, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        String nameLabel = _vocabulary.getLabel_objName();
        result.addProperty(nameLabel, teacher.getName());
        return result;
    }
}
