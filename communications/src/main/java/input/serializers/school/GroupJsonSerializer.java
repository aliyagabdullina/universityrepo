package input.serializers.school;

import com.google.gson.*;
import group.Group;
import input.serializers.VocabularySchoolJsonProperties;
import person.Student;

import java.lang.reflect.Type;

public class GroupJsonSerializer implements JsonSerializer<Group> {
    private final VocabularySchoolJsonProperties _vocabulary;

    public GroupJsonSerializer(VocabularySchoolJsonProperties vocabulary) {
        _vocabulary = vocabulary;
    }

    @Override
    public JsonElement serialize(Group group, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        String nameLabel = _vocabulary.getLabel_objName();
        result.addProperty(nameLabel, group.getName());

        String studentsLabel = _vocabulary.getLabel_groupStudents();
        JsonArray students = new JsonArray();
        group.getStudents().map(Student::getName).forEach(students::add);

        result.add(studentsLabel, students);
        return result;
    }
}
