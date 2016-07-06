package me.staartvin.statz.hooks;

import org.bukkit.plugin.Plugin;

import me.staartvin.statz.Statz;
import net.md_5.bungee.api.ChatColor;

public class HooksManager {

	private Statz plugin;
	
	public HooksManager(Statz plugin) {
		this.plugin = plugin;
	}
	
	public void checkHooks() {
		for (Dependency dep : Dependency.values()) {
			if (!isAvailable(dep)) {
				switch(dep) {
					case VOTIFIER:
						plugin.debugMessage(ChatColor.RED + "Votifier could not be found and so Statz doesn't record votes!");
						break;
				}
			}
		}
	}
	
	public boolean isAvailable(Dependency dep) {
		Plugin javaPlugin = plugin.getServer().getPluginManager().getPlugin(dep.getInternalString());
		
		if (javaPlugin == null) return false;
		
		if (!javaPlugin.isEnabled()) return false;
		
		return true;
	}
}
