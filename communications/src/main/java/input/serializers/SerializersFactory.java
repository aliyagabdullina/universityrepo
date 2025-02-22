package input.serializers;

import com.google.gson.JsonSerializer;

public interface SerializersFactory {
    JsonSerializer createSerializer(Class clazz);
}
