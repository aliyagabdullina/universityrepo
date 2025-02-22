package output;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyFileWriterJson<T> implements MyFileWriter<T> {

    @Override
    public void writeToFile(T t, File file) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setPrettyPrinting();

        registerAdapters(gsonBuilder);
        Gson gson = gsonBuilder.create();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        gson.toJson(t, bw);
        bw.close();
    }

    protected void registerAdapters(GsonBuilder gsonBuilder) {}

}
