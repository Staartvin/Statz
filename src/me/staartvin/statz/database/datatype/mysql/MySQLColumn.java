package me.staartvin.statz.database.datatype.mysql;

import me.staartvin.statz.database.datatype.Column;
import me.staartvin.statz.database.datatype.Table.SQLDataType;

public class MySQLColumn extends Column {

	public MySQLColumn(String columnName, boolean primaryKey, SQLDataType dataType) {
		super(columnName, primaryKey, dataType);
	}

}
