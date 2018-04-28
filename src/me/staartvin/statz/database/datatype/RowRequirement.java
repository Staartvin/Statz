package me.staartvin.statz.database.datatype;

/**
 * This represents a requirement for a row of data from the database.
 * <br>Let's say you want to check how many cows a player killed.
 * <br>You would do this by calling 
 * <br><code>API.getSpecificData(PlayerStat.KILLS_MOBS, uuid of player, new RowRequirement("mob", "COW"));</code>
 * <br>You can add an infinite amount of RowRequirements to the getSpecificData() method. 
 * <br>Each RowRequirement represents exactly one column that has to have a specific value.
 * <br>
 * <br>Another example: you want to know how far a player has travelled on a horse:
 * <code>getSpecificData(PlayerStat.DISTANCE_TRAVELLED, uuid of player, new RowRequirement("moveType", "HORSE"));</code>
 * <br>
 * <br>A final example: you want to know how many apples a player has eaten on world 'foodWorld':
 * <code>getSpecificData(PlayerStat.FOOD_EATEN, uuid of player, new RowRequirement("foodEaten", "APPLE"), new RowRequirement("world", "foodWorld"));</code>
 * @author Staartvin
 *
 */
public class RowRequirement {

	private String columnName, columnValue;

	public RowRequirement(String columnName, String columnValue) {
		this.setColumnName(columnName);
		this.setColumnValue(columnValue);
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}

	@Override
	public String toString() {
		return "Condition where " + columnName + " = " + columnValue;
	}
}
