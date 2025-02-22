package table;

public class TableImpl<H, D> implements Table<H, D> {
    private final H[] _headers;
    private final D[][] _data;
    private final int _numColumns;
    private final int _numDataRows;

    public TableImpl(H[] headers, D[][] data) {
        _headers = headers;
        _data = data;
        _numColumns = _headers.length;
        _numDataRows = _data.length;
    }

    @Override
    public H[] getHeaders() {
        return _headers;
    }

    @Override
    public D[][] getData() {
        return _data;
    }

    @Override
    public int getDataRowsNum() {
        return _numDataRows;
    }

    @Override
    public int getColumnsNum() {
        return _numColumns;
    }

    @Override
    public D getValueAt(int row, int column) {
        return _data[row][column];
    }
}
