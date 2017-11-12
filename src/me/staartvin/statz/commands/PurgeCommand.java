package me.staartvin.statz.commands;

import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.manager.StatzCommand;
import me.staartvin.statz.language.Lang;
import me.staartvin.statz.util.StatzUtil;
import me.staartvin.statz.util.StatzUtil.Time;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PurgeCommand extends StatzCommand {

    private final Statz plugin;

    public PurgeCommand(final Statz instance) {
        this.setUsage("/statz purge <time>");
        this.setDesc("Purge data of users that have not been online for a while.");
        this.setPermission("statz.purge");

        plugin = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (args.length < 2) {
            sender.sendMessage(Lang.INCORRECT_COMMAND_USAGE.getConfigValue(this.getUsage()));
            return true;
        }

        String timeString = args[1];

        if (!timeString.contains("h") && !timeString.contains("d") && !timeString.contains("m")) {
            sender.sendMessage(ChatColor.RED + "Incorrect time format specified.");
            sender.sendMessage(ChatColor.GOLD + "Make sure your time is formatted like this: 10d 4h 10m (or something" +
                    " like that)");
            return true;
        }

        // Time in minutes
        int timeThreshold = StatzUtil.stringToTime(timeString, Time.MINUTES);

        // Do time check and add security check
        if (timeThreshold < 5) {
            sender.sendMessage(ChatColor.RED + "I cannot purge data of users that were online less than 5 minutes ago" +
                    ".");
            sender.sendMessage(ChatColor.GOLD + "Make sure your time is formatted like this: 10d 4h 10m (or something" +
                    " like that)");
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "Purging data of players that were last online " + ChatColor.RED
                + StatzUtil.timeToString(timeThreshold, Time.MINUTES) + " ago...");

        int count = 0;

        for (OfflinePlayer player : plugin.getServer().getOfflinePlayers()) {
            if (player == null)
                continue;

            long lastPlayed = System.currentTimeMillis() - player.getLastPlayed();

            if (lastPlayed <= 0)
                continue;

            // Player has not been offline for more than the threshold, so ignore him/her.
            if ((lastPlayed / 60000) < timeThreshold)
                continue;

            UUID uuid = player.getUniqueId();

            // Sanity check
            if (uuid == null)
                continue;

            // Kick player if he is online to make sure nothing gets mixed up.
            if (player.isOnline()) {
                Player p = (Player) player;

                p.kickPlayer(ChatColor.RED + "Your database records have been reset, please log back in.");
            }

            plugin.debugMessage("Purging player " + uuid + " from database!");
            plugin.getLogsManager().writeToLogFile(sender.getName() + " purged player " + uuid);

            plugin.getDatabaseConnector().purgeData(uuid);

            count++;
        }

        sender.sendMessage(ChatColor.GREEN + "Successfully removed " + count + " players from the database.");

        return true;
    }

    /* (non-Javadoc)
     * @see me.staartvin.statz.commands.manager.StatzCommand#onTabComplete(org.bukkit.command.CommandSender, org
     * .bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
                                      final String[] args) {
        // TODO Auto-generated method stub
        return null;
    }
}
