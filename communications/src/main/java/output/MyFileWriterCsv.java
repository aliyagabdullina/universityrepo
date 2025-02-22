package output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class MyFileWriterCsv<T> implements MyFileWriter<List<T>> {
    private final List<String> _headers;
    private final Function<T, List<String>> _toDataLines;

    public MyFileWriterCsv(List<String> headers, Function<T, List<String>> toDataLines) {
        _headers = headers;
        _toDataLines = toDataLines;
    }

    @Override
    public void writeToFile(List<T> tList, File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String header = getDelimitedString(_headers);
        writer.write(header);
        tList
            .stream()
            .sequential()
            .map(_toDataLines)
            .map(this::getDelimitedString)
            .forEach(str -> {
                try {
                    writer.write(str);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        writer.close();
    }

    private String getDelimitedString(List<String> list) {
        StringBuilder result = new StringBuilder();
        for(int i=0; i <list.size()-1; i++) {
            result.append(list.get(i)).append(",");
        }
        result.append(list.get(list.size() - 1)).append("\n");
        return result.toString();
    }

}
