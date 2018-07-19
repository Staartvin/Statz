package me.staartvin.statz.datamanager.player;

import org.bukkit.Material;

/**
 * Stats that are recorded for a player by Statz
 * <p>
 * @author Staartvin
 *
 */
public enum PlayerStat {

	/**
	 * How many times did a player join the server?
	 */
    JOINS("joins", Material.ACACIA_DOOR, "How many times did I log in?"),
	/**
	 * How many times did a player die?
	 */
	DEATHS("deaths", Material.SIGN, "How many times did I die?"),
	/**
	 * How many times did a player catch an item with fishing?
	 */
    ITEMS_CAUGHT("items_caught", Material.GOLDEN_SHOVEL, "How many items did I catch while fishing?"),
	/**
	 * What kind of blocks (and how many) where placed by a player?
	 */
	BLOCKS_PLACED("blocks_placed", Material.COBBLESTONE, "How many blocks did I place?"),
	/**
	 * What kind of blocks (and how many) where broken by a player?
	 */
	BLOCKS_BROKEN("blocks_broken", Material.IRON_PICKAXE, "How many blocks did I break?"),

	/**
	 * What kind of mobs did a player kill?
	 */
    KILLS_MOBS("kills_mobs", Material.SKELETON_SKULL, "How many mobs did I kill?"),

	/**
	 * How many players did a player kill?
	 */
	KILLS_PLAYERS("kills_players", Material.DIAMOND_SWORD, "How many players did I kill?"),

	/**
	 * How many minutes has a player played on the server?
	 */
    TIME_PLAYED("time_played", Material.CLOCK, "How many minutes have I played on this server?"),

	/**
	 * What food has a player eaten?
	 */
    FOOD_EATEN("food_eaten", Material.COOKED_PORKCHOP, "How much food have I eaten?"),

	/**
	 * How much damage has a player taken?
	 * Uses Spigot's {@link org.bukkit.event.entity.EntityDamageEvent.DamageCause} class.
	 */
	DAMAGE_TAKEN("damage_taken", Material.ROTTEN_FLESH, "How much damage did I take?"),

	/**
	 * How many times did a player shear sheep?
	 */
	TIMES_SHORN("times_shorn", Material.SHEARS, "How many sheep have I shorn?"),

	/**
	 * How far and in what way did a player travel?
	 */
	DISTANCE_TRAVELLED("distance_travelled", Material.MINECART, "How far have I travelled?"),

	/**
	 * What kind of items did a player craft?
	 */
    ITEMS_CRAFTED("items_crafted", Material.CRAFTING_TABLE, "How many items did I craft?"),

	/**
	 * How much XP did a player gain in total?
	 */
    XP_GAINED("xp_gained", Material.EXPERIENCE_BOTTLE, "How much EXP did I gain?"),

	/**
	 * How many times did a player vote (with Votifier)?
	 */
	VOTES("votes", Material.MAP, "How many times did I vote?"),

	/**
	 * What are the names of corresponding UUIDs (internal database)
	 */
	PLAYERS("players"),

	/**
	 * How many arrows did a player shoot and on what world?
	 */
	ARROWS_SHOT("arrows_shot", Material.ARROW, "How many arrows did I shoot?"),

	/**
	 * How many times did a player enter a bed and on what world?
	 */
    ENTERED_BEDS("entered_beds", Material.PINK_BED, "How many times have I slept in a bed?"),

	/**
	 * What commands did a player perform and on what world?
	 */
	COMMANDS_PERFORMED("commands_performed", Material.CAKE, "How many commands did I perform?"),

	/**
	 * How many times has a player been kicked?
	 */
	TIMES_KICKED("times_kicked", Material.DISPENSER, "How many times did I get kicked?"),

	/**
	 * How many tools did a player break?
	 */
	TOOLS_BROKEN("tools_broken", Material.ANVIL, "How many tools did I break?"),

	/**
	 * How many eggs did a player throw?
	 */
	EGGS_THROWN("eggs_thrown", Material.EGG, "How many egg did I throw?"),

	/**
	 * How many times did a player switch worlds?
	 */
	WORLDS_CHANGED("worlds_changed", Material.NETHER_STAR, "How many times did I travel between worlds?"),

	/**
	 * How many buckets did a player fill?
	 */
	BUCKETS_FILLED("buckets_filled", Material.WATER_BUCKET, "How many buckets did I fill?"),

	/**
	 * How many buckets did a player empty?
	 */
	BUCKETS_EMPTIED("buckets_emptied", Material.BUCKET, "How many buckets did I empty?"),

	/**
	 * How many items did a player drop?
	 */
	ITEMS_DROPPED("items_dropped", Material.CHEST, "How many items have I dropped?"),

	/**
	 * How many items did a player pick up?
	 */
    ITEMS_PICKED_UP("items_picked_up", Material.WHEAT_SEEDS, "How many items did I pick up?"),

	/**
	 * How many times did a player teleport?
	 */
	TELEPORTS("teleports", Material.MELON_SEEDS, "How many times did I teleport?"),

	/**
	 * How many trades with villages did a player do?
	 */
	VILLAGER_TRADES("villager_trades", Material.EMERALD, "How many times did I trade with Villagers?");

	private String tableName;

	// Default value is chest
	private Material iconMaterial = Material.CHEST;

	private String humanFriendlyName;

	PlayerStat(final String tableName) {
		this.setTableName(tableName);
	}

	PlayerStat(final String tableName, final Material material) {
	    this.setTableName(tableName);
	    this.setIconMaterial(material);
    }

    /**
     * Register a new PlayerStat.
     *
     * @param tableName         Name where data will be stored.
     * @param material          Material of icon that will be shown in the GUI.
     * @param humanFriendlyName Friendly name that reminds people what this statistic records.
     */
    PlayerStat(final String tableName, final Material material, final String humanFriendlyName) {
        this.setTableName(tableName);
        this.setIconMaterial(material);
        this.setHumanFriendlyName(humanFriendlyName);
    }

	public String getTableName() {
		return tableName;
	}

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

    public Material getIconMaterial() {
        return iconMaterial;
    }

    public void setIconMaterial(Material iconMaterial) {
        this.iconMaterial = iconMaterial;
    }

    public String getHumanFriendlyName() {
        return humanFriendlyName;
    }

    public void setHumanFriendlyName(String humanFriendlyName) {
        this.humanFriendlyName = humanFriendlyName;
    }
}
