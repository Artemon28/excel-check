package FileHandler;

import java.io.IOException;

import DataBaseConnection.DataBaseWriter;

public interface ExcelReader {
	void read(DataBaseWriter dbw) throws IOException;
}
