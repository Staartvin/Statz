package me.staartvin.statz.api;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.hooks.DependencyHandler;
import me.staartvin.statz.hooks.StatzDependency;
import org.bukkit.Statistic;

import java.util.List;
import java.util.UUID;

/**
 * API class of Statz that other plugins can use to retrieve information from Statz.
 * @author Staartvin
 *
 */
public class API {

	private Statz plugin;

	public API(Statz plugin) {
		this.plugin = plugin;
	}

	/**
	 * Get the total count for a stat of a specific player on a specific world.
	 * <br>The worldName can also be omitted to get the total of all worlds.
	 * <br><br><b>This method will return null if Statz does not have info about this player regarding the specific stat.</b>
	 * @param statType the {@link PlayerStat} to get info of.
	 * @param uuid The UUID of the Player.
	 * @param worldName Name of the world to get the info from, can also be null to find the total on all worlds.
	 * @return the total count of a stat. E.g. the total amount of killed players on a world (or on all worlds).
	 */
	public Double getTotalOf(final PlayerStat statType, final UUID uuid, final String worldName) {
		PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, statType);

		if (!info.isValid()) {
			return null;
		}

		double value = 0;

		List<Query> results = info.getResults();

		if (results == null || results.isEmpty())
			return value;

		if (worldName != null) {
			// Add every value that is in the proper world
			for (Query result : results) {
				if (result.getValue("world") != null
						&& result.getValue("world").toString().equalsIgnoreCase(worldName)) {
					value += Double.parseDouble(result.getValue("value").toString());
				}
			}
		} else {
			// Add every value regardless of the world
			for (Query result : results) {
				value += Double.parseDouble(result.getValue("value").toString());
			}
		}

		return value;

	}

	/**
	 * Get specific data of a specific statType for a given player.
	 * <br>You can specify an infinite amount of conditions that need to be met.
	 * <br>For example, let's say you want to check how many cows a player killed on a specific world:
	 * <br>That is done by performing <code>getSpecificData(PlayerStat.MOBS_KILLS, uuid of player, new RowRequirement("mob", "COW"), new RowRequirement("world", "worldname"));</code>
	 * <br>
	 * <br>Another example: how much XP did a player get in total on world 'ravioli'?
	 * <br><code>getSpecificData(PlayerStat.XP_GAINED, uuid of player, new RowRequirement("world", "ravioli"));</code>
	 * @param statType The stat to get info of
	 * @param uuid UUID of the Player
	 * @param conditions Extra conditions that need to be met. If no conditions are given, this method will act the same as {@link #getTotalOf(PlayerStat, UUID, String)}.
	 * @return the total count taking the given conditions in consideration or null if no data for the given player was found.
	 */
	public Double getSpecificData(final PlayerStat statType, final UUID uuid, final RowRequirement... conditions) {
		PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, statType);

		if (!info.isValid()) {
			return null;
		}

		return info.getTotalValue(conditions);
	}

	/**
	 * Get a dependency handler of Statz. Since Statz also 'tracks' data of other plugins (in reality it provides a way to connect to other plugins,
	 * but does not actually store the information in its database). 
	 * @param dep The dependency to get the Statz handler for.
	 * @return a {@link DependencyHandler} to control data from another plugin, or null if none exists.
	 */
	public DependencyHandler getDependencyHandler(StatzDependency dep) {
		return plugin.getDependencyManager().getDependency(dep);
	}

	/**
	 * Get a value for a statistic that is stored by vanilla Minecraft.
	 * @param uuid UUID of the player to get the Statistic for.
	 * @param stat Stat to get
	 * @return an int value representing the value of the statistic.
	 */
	public int getMinecraftStatistic(UUID uuid, Statistic stat) {
		return plugin.getServer().getPlayer(uuid).getStatistic(stat);
	}
}
