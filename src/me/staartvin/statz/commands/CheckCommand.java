package me.staartvin.statz.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.manager.StatzCommand;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;
import me.staartvin.statz.util.StatzUtil.Time;
import net.md_5.bungee.api.ChatColor;

public class CheckCommand extends StatzCommand {

	private final Statz plugin;

	public CheckCommand(final Statz instance) {
		this.setUsage("/statz check <player>");
		this.setDesc("Check the stats of a player.");
		this.setPermission("statz.check");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		final String playerName;
		final UUID uuid;
		
		if (args.length == 1) {
			// No name was given, so use player's own name
			
			// Only players are allowed to check themselves.
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command can only be performed by players!");
				return true;
			}
			
			Player player = (Player) sender;
			
			playerName = player.getName();
			
			uuid = player.getUniqueId();
		} else {
			
			String targetName = args[1];
			
			@SuppressWarnings("deprecation")
			OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(targetName);
			
			if (!targetPlayer.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.RED + targetName + " has never played on this server before!");
				return true;
			}
			
			if (targetPlayer.isOnline()) {
				Player player = (Player) targetPlayer.getPlayer();
				
				playerName = player.getName();
				uuid = player.getUniqueId();
			} else {
				playerName = targetPlayer.getName();
				uuid = targetPlayer.getUniqueId();
			}
			
			if (playerName == null || uuid == null) {
				sender.sendMessage(ChatColor.RED + "Could not find player " + targetName + "!");
				return true;
			}
		}
		
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				String firstPart = ChatColor.DARK_AQUA + playerName;
				
				List<String> messages = new ArrayList<>();
				
				messages.add(ChatColor.YELLOW + "---------------- [Statz of " + playerName + "] ----------------");
				
				for (PlayerStat statType : PlayerStat.values()) {
					PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, statType);
					
					// Only use valid info.
					if (!info.isValid() || statType == PlayerStat.PLAYERS) continue;
					
					switch (statType) {
						case JOINS:
							messages.add(firstPart + " has joined " + ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
							break;
						case DEATHS:
							messages.add(firstPart + " has died " + ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
							break;
						case ITEMS_CAUGHT:
							messages.add(firstPart + " has caught " + ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " items while fishing");
							break;
						case BLOCKS_PLACED:
							messages.add(firstPart + " has placed " + ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " blocks");
							break;
						case BLOCKS_BROKEN:
							messages.add(firstPart + " has broken " + ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " blocks");
							break;
						case KILLS_MOBS:
							messages.add(firstPart + " has killed " + ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " mobs");
							break;
						case KILLS_PLAYERS:
							messages.add(firstPart + " has killed " + ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " players");
							break;
						case TIME_PLAYED:
							messages.add(firstPart + " has played for " + ChatColor.GREEN + StatzUtil.timeToString((int) info.getTotalValue(), Time.MINUTES));
							break;
						case FOOD_EATEN:
							messages.add(firstPart + " has eaten " + ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
							break;
						case DAMAGE_TAKEN:
							messages.add(firstPart + " has taken " + ChatColor.GREEN + info.getTotalValue(2) + ChatColor.DARK_AQUA + " points of damage");
							break;
						case TIMES_SHORN:
							messages.add(firstPart + " has shorn " + ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " sheep");
							break;
						case DISTANCE_TRAVELLED:
							messages.add(firstPart + " has travelled " + ChatColor.GREEN + info.getTotalValue(2) + ChatColor.DARK_AQUA + " blocks");
							break;
						case ITEMS_CRAFTED:
							messages.add(firstPart + " has crafted " + ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " items");
							break;
						case XP_GAINED:
							messages.add(firstPart + " has gained " + ChatColor.GREEN + info.getTotalValue() + ChatColor.DARK_AQUA + " exp");
							break;
						case VOTES:
							messages.add(firstPart + " has voted " + ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
							break;
						default:
							messages.add("Unknown stat '" + statType + "'.");
							break;
					}
				}
				
				for (String message: messages) {
					sender.sendMessage(message);
				}
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
