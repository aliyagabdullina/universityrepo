package input.json.serializers.school;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import course.Course;
import input.json.serializers.VocabularySchoolJsonProperties;

import java.lang.reflect.Type;

public class CourseJsonSerializer implements JsonSerializer<Course> {
        private final VocabularySchoolJsonProperties _vocabulary;

        public CourseJsonSerializer(VocabularySchoolJsonProperties vocabulary) {
                _vocabulary = vocabulary;
        }

        @Override
        public JsonElement serialize(Course course, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject result = new JsonObject();
                String nameLabel = _vocabulary.getLabel_objName();
                result.addProperty(nameLabel, course.getName());
                return result;
        }
}
