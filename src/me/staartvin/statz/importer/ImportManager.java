package me.staartvin.statz.importer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.DependencyHandler;
import me.staartvin.statz.hooks.handlers.StatsAPIHandler;
import me.staartvin.statz.util.StatzUtil;
import nl.lolmewn.stats.api.stat.Stat;
import nl.lolmewn.stats.api.stat.StatEntry;
import nl.lolmewn.stats.api.user.StatsHolder;
import nl.lolmewn.stats.stats.Arrows;
import nl.lolmewn.stats.stats.BedEnter;
import nl.lolmewn.stats.stats.BlockBreak;
import nl.lolmewn.stats.stats.BlockPlace;
import nl.lolmewn.stats.stats.BucketEmpty;
import nl.lolmewn.stats.stats.BucketFill;
import nl.lolmewn.stats.stats.CommandsDone;
import nl.lolmewn.stats.stats.DamageTaken;
import nl.lolmewn.stats.stats.Death;
import nl.lolmewn.stats.stats.EggsThrown;
import nl.lolmewn.stats.stats.FishCaught;
import nl.lolmewn.stats.stats.ItemsCrafted;
import nl.lolmewn.stats.stats.ItemsDropped;
import nl.lolmewn.stats.stats.ItemsPickedUp;
import nl.lolmewn.stats.stats.Joins;
import nl.lolmewn.stats.stats.Kill;
import nl.lolmewn.stats.stats.Move;
import nl.lolmewn.stats.stats.Omnomnom;
import nl.lolmewn.stats.stats.PVP;
import nl.lolmewn.stats.stats.Playtime;
import nl.lolmewn.stats.stats.Shears;
import nl.lolmewn.stats.stats.Teleports;
import nl.lolmewn.stats.stats.TimesKicked;
import nl.lolmewn.stats.stats.ToolsBroken;
import nl.lolmewn.stats.stats.Trades;
import nl.lolmewn.stats.stats.WorldChanged;
import nl.lolmewn.stats.stats.XpGained;

/**
 * This class can import data into Statz' database from other plugins.
 * Currently supports Stats3 (https://dev.bukkit.org/bukkit-plugins/lolmewnstats/)
 * @author Staartvin
 *
 */
public class ImportManager {

	private Statz plugin;

	public ImportManager(Statz plugin) {
		this.plugin = plugin;
	}

	/**
	 * Import data from Stats 3.
	 * @return number of entries imported from Stats 3.
	 */
	@SuppressWarnings("deprecation")
	public int importFromStats3() {
		int importedEntries = 0;

		DependencyHandler handler = plugin.getDependencyManager().getDependency(Dependency.STATS);

		if (!handler.isAvailable()) {
			plugin.getLogger().warning("Cannot import data from Stats 3 as it is not running!");

			return 0;
		}

		StatsAPIHandler stats3 = (StatsAPIHandler) handler;

		List<UUID> loggedPlayers = stats3.getLoggedPlayers();

		for (UUID uuid : loggedPlayers) {
			StatsHolder user = stats3.getStatsHolder(uuid);

			int changeCount = 0;

			Collection<Stat> storedStats = user.getStats();

			for (Stat stat : storedStats) {
				for (StatEntry entry : user.getStats(stat)) {

					Map<String, Object> metadata = entry.getMetadata();

					double value = entry.getValue();
					String worldName = (String) metadata.getOrDefault("world", "world");

					if (stat instanceof Move) {
						// Movement stat

						double moveType = Double.parseDouble(metadata.getOrDefault("type", 0).toString());

						String movementType = "WALK";

						switch ((int) moveType) {
							case 0:
								movementType = "WALK";
								break;
							case 1:
								movementType = "BOAT";
								break;
							case 2:
								movementType = "MINECART";
								break;
							case 3:
								movementType = "PIG";
								break;
							case 4:
								movementType = "PIG IN MINECART";
								break;
							case 5:
								movementType = "HORSE";
								break;
							case 6:
								movementType = "FLY";
								break;
						}

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.DISTANCE_TRAVELLED,
								StatzUtil.makeQuery("value", (value), "moveType", movementType, "world", worldName));

						changeCount++;

					} else if (stat instanceof Kill) {
						// Kill stat

						EntityType entity = EntityType.fromName((String) metadata.getOrDefault("entityType", "PIG"));

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.KILLS_MOBS,
								StatzUtil.makeQuery("value", (value), "mob", entity.toString(), "world", worldName));

						changeCount++;

					} else if (stat instanceof Teleports) {
						// Teleport stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.TELEPORTS, StatzUtil.makeQuery("value",
								(value), "world", "UNKNOWN", "destWorld", worldName, "cause", "UNKNOWN"));

						changeCount++;
					} else if (stat instanceof Arrows) {
						// Arrows shot stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ARROWS_SHOT,
								StatzUtil.makeQuery("value", (value), "world", worldName, "forceShot", 1));

						changeCount++;
					} else if (stat instanceof BedEnter) {
						// Beds entered stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ENTERED_BEDS,
								StatzUtil.makeQuery("value", (value), "world", worldName));

						changeCount++;
					} else if (stat instanceof BlockBreak) {
						// Blocks broken stat

						int dataValue = Integer.parseInt(metadata.getOrDefault("data", 0).toString());
						String blockName = (String) metadata.getOrDefault("name", "GRASS");

						Material material = Material.getMaterial(blockName);

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.BLOCKS_BROKEN,
								StatzUtil.makeQuery("value", (value), "world", worldName, "datavalue", dataValue,
										"typeid", material.getId()));

						changeCount++;
					} else if (stat instanceof BlockPlace) {
						// Blocks placed stat

						int dataValue = Integer.parseInt(metadata.getOrDefault("data", 0).toString());
						String blockName = (String) metadata.getOrDefault("name", "GRASS");

						Material material = Material.getMaterial(blockName);

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.BLOCKS_PLACED,
								StatzUtil.makeQuery("value", (value), "world", worldName, "datavalue", dataValue,
										"typeid", material.getId()));

						changeCount++;
					} else if (stat instanceof BucketEmpty) {
						// Buckets emptied stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.BUCKETS_EMPTIED,
								StatzUtil.makeQuery("value", (value), "world", worldName));

						changeCount++;
					} else if (stat instanceof BucketFill) {
						// Buckets filled stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.BUCKETS_FILLED,
								StatzUtil.makeQuery("value", (value), "world", worldName));

						changeCount++;
					} else if (stat instanceof CommandsDone) {
						// Commands performed stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.COMMANDS_PERFORMED, StatzUtil.makeQuery(
								"value", (value), "world", worldName, "command", "UNKNOWN", "arguments", "UNKNOWN"));

						changeCount++;
					} else if (stat instanceof DamageTaken) {
						// Damage taken stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.DAMAGE_TAKEN, StatzUtil
								.makeQuery("value", (value), "world", worldName, "cause", metadata.get("cause")));

						changeCount++;
					} else if (stat instanceof Death) {
						// Times died stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.DEATHS, StatzUtil
								.makeQuery("value", (value), "world", worldName));

						changeCount++;
					} else if (stat instanceof EggsThrown) {
						// Eggs thrown stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.EGGS_THROWN, StatzUtil
								.makeQuery("value", (value), "world", worldName));

						changeCount++;
					}else if (stat instanceof FishCaught) {
						// Fish caught stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ITEMS_CAUGHT, StatzUtil
								.makeQuery("value", (value), "world", worldName, "caught", "UNKNOWN"));

						changeCount++;
					}else if (stat instanceof ItemsCrafted) {
						// Items crafted stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ITEMS_CRAFTED, StatzUtil
								.makeQuery("value", (value), "world", worldName, "item", metadata.get("name")));

						changeCount++;
					} else if (stat instanceof ItemsDropped) {
						// Items dropped stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ITEMS_DROPPED, StatzUtil
								.makeQuery("value", (value), "world", worldName, "item", metadata.get("name")));

						changeCount++;
					}  else if (stat instanceof ItemsPickedUp) {
						// Items picked up stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ITEMS_PICKED_UP, StatzUtil
								.makeQuery("value", (value), "world", worldName, "item", metadata.get("name")));

						changeCount++;
					}  else if (stat instanceof ItemsDropped) {
						// Items dropped stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.ITEMS_DROPPED, StatzUtil
								.makeQuery("value", (value), "world", worldName, "item", metadata.get("name")));

						changeCount++;
					}  else if (stat instanceof Joins) {
						// Times joined stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.JOINS, StatzUtil
								.makeQuery("value", (value)));

						changeCount++;
					} else if (stat instanceof Omnomnom) {
						// Food eaten stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.FOOD_EATEN, StatzUtil
								.makeQuery("value", (value), "world", worldName, "foodEaten", "UNKNOWN"));

						changeCount++;
					}  else if (stat instanceof Playtime) {
						// Time played stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.TIME_PLAYED, StatzUtil
								.makeQuery("value", (value / 60d /* Stats records in seconds, Statz does it in minutes */), "world", worldName));

						changeCount++;
					}   else if (stat instanceof PVP) {
						// Number of players killed stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.KILLS_PLAYERS, StatzUtil
								.makeQuery("value", (value), "world", worldName, "playerKilled", metadata.get("victim")));

						changeCount++;
					} else if (stat instanceof Shears) {
						// Number of sheep shorn stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.TIMES_SHORN, StatzUtil
								.makeQuery("value", (value), "world", worldName));

						changeCount++;
					} else if (stat instanceof WorldChanged) {
						// Times changed of worlds stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.WORLDS_CHANGED, StatzUtil
								.makeQuery("value", (value), "world", "UNKNOWN", "destWorld", "UNKNOWN"));

						changeCount++;
					}  else if (stat instanceof TimesKicked) {
						// Times kicked stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.TIMES_KICKED, StatzUtil
								.makeQuery("value", (value), "world", worldName, "reason", "UNKNOWN"));

						changeCount++;
					}  else if (stat instanceof ToolsBroken) {
						// Tools broken stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.TOOLS_BROKEN, StatzUtil
								.makeQuery("value", (value), "world", worldName, "item", metadata.get("name")));

						changeCount++;
					}   else if (stat instanceof Trades) {
						// Number of trades made stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.VILLAGER_TRADES, StatzUtil
								.makeQuery("value", (value), "world", worldName, "trade", "UNKNOWN"));

						changeCount++;
					}   else if (stat instanceof XpGained) {
						// XP gained stat

						plugin.getDataManager().setPlayerInfo(uuid, PlayerStat.XP_GAINED, StatzUtil
								.makeQuery("value", (value), "world", worldName));

						changeCount++;
					}

				}

			}

			// Add added change count
			importedEntries += changeCount;
		}

		return importedEntries;
	}

}
