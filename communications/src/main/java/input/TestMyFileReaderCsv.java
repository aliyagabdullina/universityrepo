package input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import table.Table;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestMyFileReaderCsv {
    MyFileReader<Table<String, String>> reader;
    @BeforeEach
    void setUp() {
        reader = new MyFileReaderCsv();
    }

    @Test
    void readFromFile() {
        try {
            var table = reader.readFromFile(new File("src/input/testData/testCsv.csv"));
            assertNotNull(table);
            assertEquals(3, table.getHeaders().length);
            assertEquals(3, table.getDataRowsNum());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}