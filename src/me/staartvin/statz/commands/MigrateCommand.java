package me.staartvin.statz.commands;

import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.manager.StatzCommand;
import me.staartvin.statz.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MigrateCommand extends StatzCommand {

    private final Statz plugin;

    public MigrateCommand(final Statz instance) {
        this.setUsage("/statz migrate <type>");
        this.setDesc("Migrates data from a database into Statz's database");
        this.setPermission("statz.migrate");

        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (args.length < 2) {
            sender.sendMessage(Lang.INCORRECT_COMMAND_USAGE.getConfigValue(this.getUsage()));
            return true;
        }

        if (args[1].equalsIgnoreCase("stats3")) {
            // Could not find Stats 3 - abort mission
            if (!plugin.getDependencyManager().isAvailable(Library.STATS)) {
                sender.sendMessage(Lang.DID_NOT_FIND_DEPENDENCY.getConfigValue("Stats 3"));
                return true;
            }

            // Start migrating entries on separate thread.
            sender.sendMessage(ChatColor.YELLOW + "Start migrating data from Stats 3 to Statz!");
            sender.sendMessage(ChatColor.RED + "This may take a while.");

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                public void run() {
                    int changes = plugin.getImportManager().importFromStats3();

                    plugin.getLogsManager().writeToLogFile("Done importing from Stats 3 database. It may still take a" +
                            " while for Statz to update its database.");

                    sender.sendMessage(ChatColor.GREEN + "Imported " + changes + " entries from Stats 3 database.");
                }
            });
        } else if (args[1].equalsIgnoreCase("minecraft")) {
            sender.sendMessage(org.bukkit.ChatColor.RED + "NOT IMPLEMENTED YET.");
        } else {
            sender.sendMessage(org.bukkit.ChatColor.RED + "You provided an invalid migration type. You can only use " +
                    "'stats3' or 'minecraft'.");
        }

        return true;
    }

    /* (non-Javadoc)
     * @see me.staartvin.statz.commands.manager.StatzCommand#onTabComplete(org.bukkit.command.CommandSender, org
     * .bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
                                      final String[] args) {

        return null;
    }
}
