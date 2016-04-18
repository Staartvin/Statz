package me.staartvin.statz.datamanager;

/**
 * Stats that are recorded for a player by Statz
 * <p>
 */
public enum PlayerStat {

	/**
	 * How many times did a player join the server?
	 */
	JOINS("joins"),
	/**
	 * How many times did a player die?
	 */
	DEATHS("deaths"),
	/**
	 * How many times did a player catch an item with fishing?
	 */
	ITEMS_CAUGHT("items_caught");

	private String tableName;

	PlayerStat(final String tableName) {
		this.setTableName(tableName);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

}
