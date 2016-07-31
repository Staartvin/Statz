package me.staartvin.statz.commands.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.CheckCommand;
import me.staartvin.statz.commands.HelpCommand;
import me.staartvin.statz.commands.HooksCommand;
import me.staartvin.statz.commands.InfoCommand;
import me.staartvin.statz.commands.TransferCommand;
import me.staartvin.statz.util.StatzUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

/**
 * This class will manage all incoming command requests.
 * Commands are not performed here, they are only send to the correct place.
 * A specific {@linkplain StatzCommand} class handles the task of performing
 * the command.
 * 
 */
public class CommandsManager implements TabExecutor {

	private final Statz plugin;

	// Use linked hashmap so that input order is kept
	private final LinkedHashMap<List<String>, StatzCommand> registeredCommands = new LinkedHashMap<List<String>, StatzCommand>();

	/**
	 * All command aliases are set up in here.
	 */
	public CommandsManager(final Statz plugin) {
		this.plugin = plugin;

		// Register command classes
		registeredCommands.put(Arrays.asList("help", "h"), new HelpCommand(plugin));
		registeredCommands.put(Arrays.asList("check", "c"), new CheckCommand(plugin));
		registeredCommands.put(Arrays.asList("info", "i"), new InfoCommand(plugin));
		registeredCommands.put(Arrays.asList("hooks"), new HooksCommand(plugin));
		registeredCommands.put(Arrays.asList("transfer"), new TransferCommand(plugin));
	}

	public HashMap<List<String>, StatzCommand> getRegisteredCommands() {
		return registeredCommands;
	}

	/**
	 * Gets whether the sender has the given permission.
	 * <br>
	 * Will also send a 'you don't have this permission' message if the sender
	 * does not have the given permission.
	 * 
	 * @param permission Permission to check.
	 * @param sender Sender to check.
	 * @return true if this sender has the given permission, false otherwise.
	 */
	public boolean hasPermission(final String permission, final CommandSender sender) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.RED + "You do not have the permission (" + permission + ") to perform this command");
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		if (args.length == 0) {
			sender.sendMessage(ChatColor.BLUE + "-----------------------------------------------------");
			sender.sendMessage(
					ChatColor.GOLD + "Developed by: " + ChatColor.GRAY + plugin.getDescription().getAuthors());
			sender.sendMessage(ChatColor.GOLD + "Version: " + ChatColor.GRAY + plugin.getDescription().getVersion());
			sender.sendMessage(ChatColor.YELLOW + "Type /statz help for a list of commands.");
			return true;
		}

		final String action = args[0];

		List<String> suggestions = new ArrayList<>();
		List<String> bestSuggestions = new ArrayList<>();

		// Go through every list and check if that action is in there.
		for (final Entry<List<String>, StatzCommand> entry : registeredCommands.entrySet()) {

			String suggestion = StatzUtil.findClosestSuggestion(action, entry.getKey());

			if (suggestion != null) {
				suggestions.add(suggestion);
			}

			for (final String actionString : entry.getKey()) {

				if (actionString.equalsIgnoreCase(action)) {
					return entry.getValue().onCommand(sender, cmd, label, args);
				}
			}
		}

		// Search for suggestions if argument was not found
		for (String suggestion : suggestions) {
			String[] split = suggestion.split(";");

			int editDistance = Integer.parseInt(split[1]);

			// Only give suggestion if edit distance is small
			if (editDistance <= 2) {
				bestSuggestions.add(split[0]);
			}
		}
		
		sender.sendMessage(ChatColor.RED + "Command not recognised!");
		
		if (!bestSuggestions.isEmpty()) {
			BaseComponent[] builder = new ComponentBuilder("Did you perhaps mean ").color(ChatColor.DARK_AQUA)
					.append("/statz ").color(ChatColor.GREEN).append(StatzUtil.seperateList(bestSuggestions, "or")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("These are suggestions based on your input.").create()))
					.append("?").color(ChatColor.DARK_AQUA).create();
			
			if (sender instanceof Player) {
				Player p = (Player) sender;
				
				p.spigot().sendMessage(builder);
			} else {
				sender.sendMessage(ChatColor.DARK_AQUA + "Did you perhaps mean " + ChatColor.GREEN + "/statz "
						+ StatzUtil.seperateList(bestSuggestions, "or") + ChatColor.DARK_AQUA + "?");
			}
		}

		sender.sendMessage(ChatColor.YELLOW + "Use '/statz help' for a list of commands.");
		return true;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.TabCompleter#onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel,
			final String[] args) {

		if (args.length <= 1) {
			// Show a list of commands if needed

			final List<String> commands = new ArrayList<String>();

			for (final Entry<List<String>, StatzCommand> entry : registeredCommands.entrySet()) {
				final List<String> list = entry.getKey();

				commands.add(list.get(0));
			}

			return commands;
		}

		final String subCommand = args[0].trim();

		// Return on tab complete of sub command
		for (final Entry<List<String>, StatzCommand> entry : registeredCommands.entrySet()) {

			for (final String alias : entry.getKey()) {
				if (subCommand.trim().equalsIgnoreCase(alias)) {
					return entry.getValue().onTabComplete(sender, cmd, commandLabel, args);
				}
			}

		}

		return null;
	}
}
