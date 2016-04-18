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
	ITEMS_CAUGHT("items_caught"),
	/**
	 * What kind of blocks (and how many) where placed by a player?
	 */
	BLOCKS_PLACED("blocks_placed"),
	/**
	 * What kind of blocks (and how many) where broken by a player?
	 */
	BLOCKS_BROKEN("blocks_broken");

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
