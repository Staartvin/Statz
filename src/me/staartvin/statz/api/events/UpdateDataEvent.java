package me.staartvin.statz.api.events;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.PlayerStat;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.List;

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


	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
