package me.staartvin.statz.datamanager;

import java.util.UUID;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.SQLiteTable;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

/**
 * This class handles all incoming data queries from other plugins (and from internal calls).
 * <br>Getting info of a player should be done here.
 * <p>
 * Date created:  15:03:12
 * 17 apr. 2016
 * @author "Staartvin"
 *
 */
public class DataManager {

	private Statz plugin;
	
	public DataManager(Statz instance) {
		plugin = instance;
	}
	
	public PlayerInfo getPlayerInfo(UUID uuid, PlayerStat statType) {
		PlayerInfo info = new PlayerInfo(uuid);
		
		Object result = null;
		
		if (statType.equals(PlayerStat.JOINS)) {
			result = plugin.getSqlConnector().getObject(statType.getTableName(), "value", StatzUtil.makeQuery("uuid", uuid.toString()));
		} else if (statType.equals(PlayerStat.DEATHS)) {
			result = plugin.getSqlConnector().getObject(statType.getTableName(), "value", StatzUtil.makeQuery("uuid", uuid.toString()));
		}
		
		// Result is not null, so this is a valid player info.
		if (result != null) {
			info.setValid(true);
			
			info.setValue(statType.toString(), result.toString());
		}
		
		return info;
	}
	
	public void setPlayerInfo(UUID uuid, PlayerStat statType, Object... parameters) {
		
		String[] strings = new String[parameters.length];
		
		for (int i=0;i<parameters.length;i++) {
			strings[i] = parameters[i].toString();
		}
		
		SQLiteTable table = plugin.getSqlConnector().getSQLiteTable(statType.getTableName());
		
		plugin.getSqlConnector().setObjects(table, StatzUtil.makeQuery(strings));
	}
}
