package me.staartvin.statz.api;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;

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
	public Object getTotalOf(PlayerStat statType, UUID uuid, String worldName) {

		PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, statType);

		if (!info.isValid()) {
			return null;
		}

		double value = 0;

		List<HashMap<String, Object>> results = info.getResults();

		if (results == null || results.isEmpty())
			return value;

		if (worldName != null) {
			// Add every value that is in the proper world
			for (HashMap<String, Object> result : results) {
				if (result.get("world") != null && result.get("world").toString().equalsIgnoreCase(worldName)) {
					value += Double.parseDouble(result.get("value").toString());
				}
			}
		} else {
			// Add every value regardless of the world
			for (HashMap<String, Object> result : results) {
				value += Double.parseDouble(result.get("value").toString());
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
	public Object getSpecificData(PlayerStat statType, UUID uuid, RowRequirement... conditions) {
		PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, statType);

		if (!info.isValid()) {
			return null;
		}

		double value = 0;

		List<HashMap<String, Object>> results = info.getResults();

		if (results == null || results.isEmpty())
			return value;

		for (HashMap<String, Object> result : results) {
			boolean isValid = true;
			
			for (int i=0;i<conditions.length;i++) {
				
				RowRequirement req = conditions[i];
				// Check if each condition that was given is true.
				if (result.get(req.getColumnName()) == null || !result.get(req.getColumnName()).toString().equalsIgnoreCase(req.getColumnValue())) {
					isValid = false;
					break;
				}
			}
			
			// All conditions were met, so we add this value.
			if (isValid) {
				value += Double.parseDouble(result.get("value").toString());
			}
		}

		return value;
	}
}
