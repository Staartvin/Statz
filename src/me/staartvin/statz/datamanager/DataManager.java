package me.staartvin.statz.datamanager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.SQLiteTable;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

/**
 * This class handles all incoming data queries from other plugins (and from
 * internal calls).
 * <br>
 * Getting info of a player should be done here.
 * <p>
 * Date created: 15:03:12
 * 17 apr. 2016
 * 
 * @author "Staartvin"
 *
 */
public class DataManager {

	private final Statz plugin;

	public DataManager(final Statz instance) {
		plugin = instance;
	}

	public PlayerInfo getPlayerInfo(final UUID uuid, final PlayerStat statType) {
		final PlayerInfo info = new PlayerInfo(uuid);

		List<HashMap<String, Object>> result = plugin.getSqlConnector().getObjects(statType.getTableName(),
				StatzUtil.makeQuery("uuid", uuid.toString()));

		// Result is not null, so this is a valid player info.
		if (result != null && !result.isEmpty()) {
			info.setValid(true);

			info.setResults(result);
		}

		return info;
	}

	public void setPlayerInfo(final UUID uuid, final PlayerStat statType, LinkedHashMap<String, String> results) {

		final SQLiteTable table = plugin.getSqlConnector().getSQLiteTable(statType.getTableName());

		plugin.getSqlConnector().setObjects(table, results);
	}
}
