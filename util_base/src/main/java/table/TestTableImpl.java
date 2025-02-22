package table;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestTableImpl {
    Table<String,String> table;
    @BeforeEach
    void setUp() {
        String[] headers = new String[2];
        headers[0] = "id";
        headers[1] = "name";
        String[][] data = new String[1][2];
        data[0] = new String[2];
        data[0][0] = "0";
        data[0][1] = "Anna";
        table = new TableImpl<>(headers, data);
    }

    @Test
    void testGetHeaders() {
       var h = table.getHeaders();
        assertEquals("id", h[0]);
        assertEquals("name", h[1]);
    }

    @Test
    void testGetData() {
        var d = table.getData();
        assertEquals("Anna", d[0][1]);
    }

    @Test
    void testGetLinesNum() {
        assertEquals(1, table.getDataRowsNum());
    }

    @Test
    void getColumnsNum() {
        assertEquals(2, table.getColumnsNum());
    }
}