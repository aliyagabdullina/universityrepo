package input;

import table.Table;
import table.TableImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class MyFileReaderCsv implements MyFileReader<Table<String,String>> {
    @Override
    public Table<String, String> readFromFile(File file) throws IOException {
        List<String> lines = Files.lines(file.toPath()).sequential().toList();
        if (lines.size() == 0) {
            return new TableImpl<>(new String[0], new String[0][0]);
        }
        String[] header = parseToArray(lines.get(0));
        String[][] data = lines.stream()
                .sequential()
                .skip(1)
                .map(this::parseToArray)
                .toArray(String[][]::new);
        return new TableImpl<>(header, data);
    }
    private String[] parseToArray(String str) {
        return str.split(",");
    }
}
