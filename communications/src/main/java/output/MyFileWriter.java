package output;

import java.io.File;
import java.io.IOException;

public interface MyFileWriter<T>{
    void writeToFile(T t, File file) throws IOException;
}
