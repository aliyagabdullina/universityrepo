package dataInput;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class TestDelimitedReaderHeaderBased {
    DelimitedReader<String> reader;

    @BeforeEach
    void setUp() {
        String delimiter = ",";
        List<String> etalonHeaderSequence = List.of("surname", "name");
        Function<List<String>, String> toObjectMapper = list -> list.get(0) + " " + list.get(1);
        reader = new DelimitedReaderHeaderBased<>(delimiter, etalonHeaderSequence, toObjectMapper);
    }

    @Test
    public void testReadSuccess() {
        assertNotNull(reader.read(new File("src/dataInput/DelimitedTest.csv")));
        var result = reader.read(new File("src/dataInput/DelimitedTest.csv"));
        assertEquals(2, result.size());
        assertEquals("Ivanova Anna", result.get(0));
    }

}