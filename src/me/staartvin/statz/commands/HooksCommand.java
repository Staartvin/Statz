package me.staartvin.statz.commands;

import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.manager.StatzCommand;
import me.staartvin.statz.language.Lang;
import me.staartvin.utils.pluginlibrary.statz.Library;
import me.staartvin.utils.pluginlibrary.statz.hooks.LibraryHook;
import me.staartvin.utils.pluginlibrary.statz.hooks.StatzHook;
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

		for (final Library dep : Library.values()) {

			final LibraryHook handler = plugin.getDependencyManager()
					.getLibraryHook(dep).orElse(null);

			if (handler != null && handler.isHooked() && !(handler instanceof StatzHook)) {
				sender.sendMessage(org.bukkit.ChatColor.GRAY + "- " + org.bukkit.ChatColor.GREEN + dep
						.getHumanPluginName());
			}
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
