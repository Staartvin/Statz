package me.staartvin.statz.api.events;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This event is fired when changes are made to the Statz database. 
 * <br>This event stores the queries in the pool privately and gives other plugins the ability to read the data that has changed.
 * @author Staartvin
 *
 */
public class UpdateDataEvent extends Event {

	private Statz plugin;

	private static final HandlerList handlers = new HandlerList();

	// Pool that stores all the changes made to the database
	private HashMap<PlayerStat, List<Query>> pool = new HashMap<>();

	public UpdateDataEvent(HashMap<PlayerStat, List<Query>> pool, Statz plugin) {
		this.pool = pool;
		this.plugin = plugin;
	}

	/**
	 * Get the queries that have been sent to the server. This can be used to read what updates have been sent, but generally the
	 * {@link #getUpdatedInfo(PlayerStat)} is used for that. 
	 * @param stat PlayerStat type to get the updated queries of.
	 * @return a list of queries that were sent to the database or null if nothing was sent.
	 */
	public List<Query> getUpdateQueries(PlayerStat stat) {
		return pool.get(stat);
	}

	/**
	 * This method returns a list of playerinfo objects that represent the updates that have been send to the database. 
	 * Every playerinfo object represents one single update. Let's say Statz updated stats of 2 players, where 1 player flew for 30 blocks and walked for 20
	 * and the other player just walked for 20 blocks. There will be three PlayerInfo objects. One for the flying of player 1, one for the walking of player 1 and
	 * finally one for the walking of player 2.
	 * Each playerinfo object will only contain data about the specific stat that was updated. Let's say Statz updated that the 2 players have killed 10 sheep
	 * in world 'world'. Each playerinfo object will then only consist of this data and not of any other data that is stored in Statz's database.
	 * 
	 * <br><br><b>NOTE:</b> will return null when no updated data could be found.
	 * @param stat Stat to get the updated data of.
	 * @return a list of PlayerInfo objects that represent the updated data that has been sent to the database.
	 */
	public List<PlayerInfo> getUpdatedInfo(PlayerStat stat) {
		List<Query> updateQueries = this.getUpdateQueries(stat);

		// We didn't update this stat - so return null.
		if (updateQueries == null || updateQueries.isEmpty()) {
			return null;
		}

		List<PlayerInfo> playerInfos = new ArrayList<>();

		// Add UUIDs that are updated so we can find their updated values
		for (Query q : updateQueries) {
			UUID uuid = q.getUUID();

			if (uuid == null)
				continue;
			
			// Copy the update query, but remove the value column so we can only obtain the updated data that is relevant.
			Query copyQuery = q.getFilteredCopy("value");

			// Find all updated player info of the updated UUID.
			PlayerInfo playerInfo = plugin.getDataManager().getPlayerInfo(uuid, stat, copyQuery);

			if (playerInfo == null || !playerInfo.isValid()) {
				continue;
			}

			playerInfos.add(playerInfo);

		}

		return playerInfos;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
