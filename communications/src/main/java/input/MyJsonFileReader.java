package input;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MyJsonFileReader<T> implements MyFileReader<T> {

    private final Class<? extends Object> _deserializedClass;

    public MyJsonFileReader(Class<? extends Object> deserializedClass) {
        _deserializedClass = deserializedClass;
    }

    @Override
    public T readFromFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        T t = (T)new Gson().fromJson(reader, _deserializedClass);
        reader.close();
        return t;
    }


}
