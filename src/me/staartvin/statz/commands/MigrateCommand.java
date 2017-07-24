package me.staartvin.statz.commands;

import java.util.List;

import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.StatsHook;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.manager.StatzCommand;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.language.Lang;
import net.md_5.bungee.api.ChatColor;

public class MigrateCommand extends StatzCommand {

	private final Statz plugin;

	public MigrateCommand(final Statz instance) {
		this.setUsage("/statz migrate");
		this.setDesc("Imports data from Stats 3 into Statz's database");
		this.setPermission("statz.migrate");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		// Could not find Stats 3 - abort mission
		if (!plugin.getDependencyManager().isAvailable(Library.STATS)) {
			sender.sendMessage(Lang.DID_NOT_FIND_DEPENDENCY.getConfigValue("Stats 3"));
			return true;
		}

		// Start migrating entries on seperate thread.
		sender.sendMessage(ChatColor.YELLOW + "Start migrating data from Stats 3 to Statz!");
		sender.sendMessage(ChatColor.RED + "This may take a while.");

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				int changes = plugin.getImportManager().importFromStats3();

				while (changes == 0) {
					try {
						// Sleep for 10 seconds and then try again - Stats takes a while to load people
						// For each user that is logged, wait 0.5 seconds.
						Thread.sleep(((StatsHook) plugin.getDependencyManager().getLibraryHook(Library.STATS))
								.getLoggedPlayers().size() * 500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					changes = plugin.getImportManager().importFromStats3();
				}

				sender.sendMessage(ChatColor.GREEN + "Imported " + changes + " entries from Stats 3 database.");
			}
		});

		return true;
	}

	/* (non-Javadoc)
	 * @see me.staartvin.statz.commands.manager.StatzCommand#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
			final String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
}
