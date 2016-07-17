package me.staartvin.statz.database.datatype.sqlite;

import me.staartvin.statz.database.datatype.Column;
import me.staartvin.statz.database.datatype.Table.SQLDataType;

public class SQLiteColumn extends Column {

	public SQLiteColumn(String columnName, boolean primaryKey, SQLDataType dataType) {
		super(columnName, primaryKey, dataType);
	}

}
