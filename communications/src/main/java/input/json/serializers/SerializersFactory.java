package input.json.serializers;

import com.google.gson.JsonSerializer;

public interface SerializersFactory {
    JsonSerializer createSerializer(Class clazz);
}
