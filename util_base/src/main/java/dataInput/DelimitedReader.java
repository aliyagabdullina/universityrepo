package dataInput;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface DelimitedReader<T> {
    List<T> read(File delimitedFile);
}
