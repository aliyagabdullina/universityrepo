package table;

public interface Table<H, D> {
    H[] getHeaders();
    D[][] getData();
    int getDataRowsNum();
    int getColumnsNum();

    D getValueAt(int row, int column);
}
