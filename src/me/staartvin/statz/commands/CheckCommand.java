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
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class CheckCommand extends StatzCommand {

	private final Statz plugin;

	public CheckCommand(final Statz instance) {
		this.setUsage("/statz check <player> (page number)");
		this.setDesc("Check the stats of a player.");
		this.setPermission("statz.check");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		String playerName = null;
		UUID uuid = null;
		int pageNumber = 1;

		boolean hasGivenPlayerName = false;

		// Command with page number AND player name
		if (args.length > 2) {
			playerName = args[1];

			hasGivenPlayerName = true;

			try {
				pageNumber = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				// Third argument was not a number, so give an error
				sender.sendMessage(ChatColor.RED + "You did not provide a correct page number!");
				return true;
			}
		} else if (args.length > 1) {
			// Provided a page number or player name

			try {
				pageNumber = Integer.parseInt(args[1]);
				hasGivenPlayerName = false;
			} catch (NumberFormatException e) {
				// Second argument was not a number, so must be a player name
				playerName = args[1];
				hasGivenPlayerName = true;
			}

		} else if (args.length == 1) {
			// No player name given and no page number
			// Default to page number = 1
			hasGivenPlayerName = false;
		}

		if (!hasGivenPlayerName) {
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
			@SuppressWarnings("deprecation")
			OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(playerName);

			if (!targetPlayer.hasPlayedBefore()) {
				sender.sendMessage(ChatColor.RED + playerName + " has never played on this server before!");
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
				sender.sendMessage(ChatColor.RED + "Could not find player!");
				return true;
			}
		}

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			private String playerName;
			private UUID uuid;
			private int pageNumber;

			private Runnable init(String playerName, UUID uuid, int pageNumber) {
				this.playerName = playerName;
				this.uuid = uuid;
				this.pageNumber = pageNumber - 1;
				return this;
			}

			public void run() {

				List<String> messages = new ArrayList<>();
				List<TextComponent> messagesSpigot = new ArrayList<>();

				for (PlayerStat statType : PlayerStat.values()) {
					PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, statType);

					// Only use valid info.
					if (!info.isValid() || statType == PlayerStat.PLAYERS)
						continue;

					switch (statType) {
						case JOINS:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has joined ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" times");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has joined " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
							}

							break;
						case DEATHS:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has died ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" times");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has died " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
							}

							break;
						case ITEMS_CAUGHT:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has caught ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" items while fishing");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has caught " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " items while fishing");
							}

							break;
						case BLOCKS_PLACED:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has placed ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" blocks");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has placed " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " blocks");
							}

							break;
						case BLOCKS_BROKEN:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has broken ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" blocks");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has broken " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " blocks");
							}

							break;
						case KILLS_MOBS:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has killed ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" mobs");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has killed " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " mobs");
							}

							break;
						case KILLS_PLAYERS:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has killed ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" players");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has killed " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " players");
							}

							break;
						case TIME_PLAYED:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has played for ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent(StatzUtil.timeToString((int) info.getTotalValue(), Time.MINUTES));
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent("");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has played for " + ChatColor.GREEN
										+ StatzUtil.timeToString((int) info.getTotalValue(), Time.MINUTES));
							}

							break;
						case FOOD_EATEN:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has eaten ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" times");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has eaten " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
							}

							break;
						case DAMAGE_TAKEN:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has taken ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" points of damage");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has taken " + ChatColor.GREEN
										+ info.getTotalValue(2) + ChatColor.DARK_AQUA + " points of damage");
							}

							break;
						case TIMES_SHORN:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has shorn ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" sheep");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has shorn " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " sheep");
							}

							break;
						case DISTANCE_TRAVELLED:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has travelled ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent(info.getTotalValue(2) + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" blocks");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has travelled " + ChatColor.GREEN
										+ info.getTotalValue(2) + ChatColor.DARK_AQUA + " blocks");
							}

							break;
						case ITEMS_CRAFTED:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has crafted ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" items");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has crafted " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " items");
							}

							break;
						case XP_GAINED:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has gained ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" exp");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has gained " + ChatColor.GREEN
										+ info.getTotalValue() + ChatColor.DARK_AQUA + " exp");
							}

							break;
						case VOTES:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has voted ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" times");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has voted " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
							}

							break;
						case ARROWS_SHOT:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has fired ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" arrows");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has fired " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " arrows");
							}

							break;
						case ENTERED_BEDS:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " went sleepy sleepy in ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" beds");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " went sleepy sleepy in "
										+ ChatColor.GREEN + (int) info.getTotalValue() + ChatColor.DARK_AQUA + " beds");
							}

							break;
						case COMMANDS_PERFORMED:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has performed ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" commands");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has performed " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " commands");
							}

							break;
						case TIMES_KICKED:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has been kicked ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" times");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has been kicked " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
							}

							break;
						case TOOLS_BROKEN:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has broken ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" tools");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has broken " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " tools");
							}

							break;
						case EGGS_THROWN:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has thrown ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" unborn chickens (eggs)");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has thrown " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " unborn chickens (eggs)");
							}

							break;
						case WORLDS_CHANGED:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has changed worlds ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" times");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has changed worlds " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
							}

							break;
						case BUCKETS_FILLED:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has filled ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" buckets");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has filled " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " buckets");
							}

							break;
						case BUCKETS_EMPTIED:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has emptied ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" buckets");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has emptied " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " buckets");
							}

							break;
						case ITEMS_PICKED_UP:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has picked up ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" items");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has picked up " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " items");
							}

							break;
						case ITEMS_DROPPED:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has dropped ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" items");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has dropped " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " items");
							}

							break;
						case TELEPORTS:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has teleported ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" times");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has teleported " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times");
							}

							break;
						case VILLAGER_TRADES:
							if (sender instanceof Player) {
								TextComponent message = new TextComponent(playerName + " has traded ");
								message.setColor(ChatColor.DARK_AQUA);
								
								TextComponent partTwo = new TextComponent((int) info.getTotalValue() + "");
								partTwo.setColor(ChatColor.GREEN);
								
								TextComponent partThree = new TextComponent(" times with villagers");
								partThree.setColor(ChatColor.DARK_AQUA);
								
								message.addExtra(partTwo);
								message.addExtra(partThree);
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/statz info " + statType.toString() + " " +  playerName));
								
								messagesSpigot.add(message);
							} else {
								messages.add(ChatColor.DARK_AQUA + playerName + " has traded " + ChatColor.GREEN
										+ (int) info.getTotalValue() + ChatColor.DARK_AQUA + " times with villagers");
							}

							break;
						default:
							if (sender instanceof Player) {
								messagesSpigot.add(new TextComponent("Unknown stat '" + statType + "'."));
							} else {
								messages.add("Unknown stat '" + statType + "'.");
							}
							
							break;
					}
				}

				int messagesPerPage = 8;
				
				int pages = 0;
				
				if (sender instanceof Player) {
					pages = (int) Math.ceil(messagesSpigot.size() / (double) messagesPerPage);
				} else {
					pages = (int) Math.ceil(messages.size() / (double) messagesPerPage);
				}
				
				

				if (pageNumber > (pages - 1) || pageNumber < 0) {
					pageNumber = 0;
				}

				//for (int i = 0; i < pages; i++) {

				sender.sendMessage(
						ChatColor.YELLOW + "---------------- [Statz of " + playerName + "] ----------------");
				for (int j = 0; j < messagesPerPage; j++) {
					int index = (pageNumber == 0 ? j : (pageNumber * messagesPerPage) + j);

					// Don't try to get other messages, as there are no others.
					if (sender instanceof Player) {
						if (index >= messagesSpigot.size()) {
							break;
						}
					} else {
						if (index >= messages.size()) {
							break;
						}
					}
					

					//System.out.println("index: " + index);

					if (sender instanceof Player) {
						Player p = (Player) sender;

						p.spigot().sendMessage(messagesSpigot.get(index));
					} else {
						sender.sendMessage(messages.get(index));
					}
					
				}

				// Create page clicker
				BaseComponent[] pageClicker = new ComponentBuilder("<<< ").color(ChatColor.GOLD)
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz c " + playerName + " " + (pageNumber < 0 ? 0 : pageNumber)))
						.append("Page ").color(ChatColor.DARK_AQUA).append(pageNumber + 1 + "").color(ChatColor.GREEN)
						.append(" of " + pages).color(ChatColor.DARK_AQUA).append(" >>>").color(ChatColor.GOLD)
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/statz c " + playerName + " " + (pageNumber + 2 > pages ? pages
										: pageNumber + 2)))
						.create();

				if (sender instanceof Player) {
					Player p = (Player) sender;

					p.spigot().sendMessage(pageClicker);
				} else {
					sender.sendMessage(ChatColor.GOLD + "<<< " + ChatColor.DARK_AQUA + "Page " + ChatColor.GREEN
							+ (pageNumber + 1) + ChatColor.DARK_AQUA + " of " + pages + ChatColor.GOLD + " >>>");
				}
				//}
			}
		}.init(playerName, uuid, pageNumber));

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
