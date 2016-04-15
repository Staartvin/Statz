package me.staartvin.statz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.SQLiteTable;
import me.staartvin.statz.util.StatzUtil;
import net.md_5.bungee.api.ChatColor;

public class PlayerDeathListener implements Listener {

	private Statz plugin;
	
	public PlayerDeathListener(Statz plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onDie(PlayerDeathEvent event) {
		
		// Get player
		Player player = event.getEntity();
		
		// Get table to read and write to.
		SQLiteTable table = plugin.getSqlConnector().getSQLiteTable("death"); 
		
		Object currentStat = plugin.getSqlConnector().getObject(table, "value", StatzUtil.makeQuery("uuid", player.getUniqueId().toString()));
		
		// Get current value of stat.
		int currentValue = 0;
		
		// Only cast if currentStat is not null (hence it has an entry)
		if (currentStat != null) {
			currentValue = (int) currentStat;
		}
		
		// Update value to new stat.
		plugin.getSqlConnector().setObjects(table, StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", (currentValue + 1 ) + ""));
		
		player.sendMessage(ChatColor.RED + "Your death counter is now " + ChatColor.GOLD + (currentValue + 1));
	}
}
