package input.serializers;

import com.google.gson.JsonSerializer;
import course.Course;
import group.Group;
import input.serializers.school.CourseJsonSerializer;
import input.serializers.school.GroupJsonSerializer;
import input.serializers.school.PlaceJsonSerializer;
import input.serializers.school.TeacherJsonSerializer;
import person.Teacher;
import place.Place;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializersFactoryImpl implements SerializersFactory {
    private VocabularySchoolJsonProperties _vocabulary;
    private Map<Class, JsonSerializer> _serializers;

    public SerializersFactoryImpl(VocabularySchoolJsonProperties vocabulary) {
        setVocabulary(vocabulary);
    }


    @Override
    public JsonSerializer createSerializer(Class clazz) {
       var result =  _serializers.get(clazz);
       if(result == null) {
           throw new UnsupportedOperationException("Unsupported serializer for class " + clazz);
       }
       return result;
    }

    public void setVocabulary(VocabularySchoolJsonProperties vocabulary) {
        this._vocabulary = vocabulary;
        _serializers = createSerializersMap();
    }

    private Map<Class, JsonSerializer> createSerializersMap() {
        Map<Class, JsonSerializer> result = new ConcurrentHashMap<>();

        var teacherSerializer = new TeacherJsonSerializer(_vocabulary);
        result.put(Teacher.class, teacherSerializer);

        var groupSerializer = new GroupJsonSerializer(_vocabulary);
        result.put(Group.class, groupSerializer);

        var courseSerializer = new CourseJsonSerializer(_vocabulary);
        result.put(Course.class, courseSerializer);

        var placeSerializer = new PlaceJsonSerializer(_vocabulary);
        result.put(Place.class, placeSerializer);

        return result;
    }


}
