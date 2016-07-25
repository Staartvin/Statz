package me.staartvin.statz.datamanager;

/**
 * Stats that are recorded for a player by Statz
 * <p>
 */
/**
 * @author Staartvin
 *
 */
/**
 * @author Staartvin
 *
 */
/**
 * @author Staartvin
 *
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
	BLOCKS_BROKEN("blocks_broken"),

	/**
	 * What kind of mobs did a player kill?
	 */
	KILLS_MOBS("kills_mobs"),

	/**
	 * How many players did a player kill?
	 */
	KILLS_PLAYERS("kills_players"),

	/**
	 * How many minutes has a player played on the server?
	 */
	TIME_PLAYED("time_played"),

	/**
	 * What food has a player eaten?
	 */
	FOOD_EATEN("food_eaten"),

	/**
	 * How much damage has a player taken?
	 * Uses Spigot's {@link org.bukkit.event.entity.EntityDamageEvent.DamageCause} class.
	 */
	DAMAGE_TAKEN("damage_taken"),

	/**
	 * How many times did a player shear sheep?
	 */
	TIMES_SHORN("times_shorn"),

	/**
	 * How far and in what way did a player travel?
	 */
	DISTANCE_TRAVELLED("distance_travelled"),

	/**
	 * What kind of items did a player craft?
	 */
	ITEMS_CRAFTED("items_crafted"),

	/**
	 * How much XP did a player gain in total?
	 */
	XP_GAINED("xp_gained"),

	/**
	 * How many times did a player vote (with Votifier)?
	 */
	VOTES("votes"),

	/**
	 * What are the names of corresponding UUIDs (internal database)
	 */
	PLAYERS("players"),
	
	/**
	 * How many arrows did a player shoot and on what world?
	 */
	ARROWS_SHOT("arrows_shot"),
	
	/**
	 * How many times did a player enter a bed and on what world?
	 */
	ENTERED_BEDS("entered_beds"),
	
	/**
	 * What commands did a player perform and on what world?
	 */
	COMMANDS_PERFORMED("commands_performed"),
	
	/**
	 * How many times has a player been kicked?
	 */
	TIMES_KICKED("times_kicked"),
	
	/**
	 * How many tools did a player break?
	 */
	TOOLS_BROKEN("tools_broken");

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
