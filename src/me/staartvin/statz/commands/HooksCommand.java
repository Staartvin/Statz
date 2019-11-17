package me.staartvin.statz.commands;

import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.manager.StatzCommand;
import me.staartvin.statz.hooks.StatzDependency;
import me.staartvin.statz.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HooksCommand extends StatzCommand {

	private final Statz plugin;

	public HooksCommand(final Statz instance) {
		this.setUsage("/statz hooks");
		this.setDesc("Show a list of plugins that Statz hooked into.");
		this.setPermission("statz.hooks");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		sender.sendMessage(Lang.STATZ_HOOKED_AND_LISTENING.getConfigValue());
		
		for (StatzDependency d: plugin.getDependencyManager().getAvailableDependencies()) {
			sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.GREEN + d.getInternalString());
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see me.staartvin.statz.commands.manager.StatzCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
			final String[] args) {

        return null;
	}
}
