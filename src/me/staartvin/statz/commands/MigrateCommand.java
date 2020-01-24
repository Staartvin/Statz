package me.staartvin.statz.commands;

import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.manager.StatzCommand;
import me.staartvin.statz.language.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
            // This type is no longer supported.
            sender.sendMessage(org.bukkit.ChatColor.RED + "This migration type is no longer supported.");
        } else if (args[1].equalsIgnoreCase("minecraft")) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        int importedPlayers = plugin.getImportManager().importFromVanilla().get();

                        sender.sendMessage(ChatColor.GREEN + "Imported statistics of " + importedPlayers + " players.");
                    } catch (InterruptedException | ExecutionException e) {
                        sender.sendMessage(ChatColor.RED + "Could not import data from Minecraft!");
                        e.printStackTrace();
                    }
                }
            });
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

        return Collections.singletonList("minecraft");
    }
}
