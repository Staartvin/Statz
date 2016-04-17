package me.staartvin.statz.datamanager;

/**
 * Stats that are recorded for a player by Statz
 * <p>
 */
public enum PlayerStat {

	/**
	 * How many times did this player join the server?
	 */
	JOINS("joins"), 
	/**
	 * How many times did this player die?
	 */
	DEATHS("deaths");
	
	private String tableName;
	
	PlayerStat(String tableName) {
		this.setTableName(tableName);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	
}
