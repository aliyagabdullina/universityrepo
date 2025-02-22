package input;

import java.io.File;
import java.io.IOException;

public interface MyFileReader<T> {
    T  readFromFile(File file) throws IOException;
}
