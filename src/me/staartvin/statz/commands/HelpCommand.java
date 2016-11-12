package me.staartvin.statz.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.manager.StatzCommand;
import me.staartvin.statz.language.Lang;

public class HelpCommand extends StatzCommand {

	private final Statz plugin;

	public HelpCommand(final Statz instance) {
		this.setUsage("/statz help <page>");
		this.setDesc("Show a list of commands.");
		this.setPermission("statz.help");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		if (args.length == 1) {
			showHelpPages(sender, 1);
		} else {
			int page = 1;
			try {
				page = Integer.parseInt(args[1]);
			} catch (final Exception e) {
				sender.sendMessage(Lang.INCORRECT_PAGE_NUMBER.getConfigValue());
				return true;
			}
			showHelpPages(sender, page);
		}
		return true;
	}

	private void showHelpPages(final CommandSender sender, int page) {
		List<StatzCommand> commands = new ArrayList<StatzCommand>(
				plugin.getCommandsManager().getRegisteredCommands().values());

		// Create a new list that will be new commands list. This is done so statz automatically adjusts help pages.

		// If sender is OP then all commands are available, no need to refactor.
		if (!sender.isOp()) {

			final List<StatzCommand> newList = new ArrayList<StatzCommand>();

			for (final StatzCommand cmd : commands) {
				// Check if player has permission to do this, before presenting this command
				if (cmd.getPermission() != null && sender.hasPermission(cmd.getPermission())) {
					newList.add(cmd);
				}
			}

			commands = newList;
		}

		final int listSize = commands.size();

		// Don't show more than 6 commands per page
		// (Does she want the D?)
		final int maxPages = (int) Math.ceil(listSize / 6D);

		if (page > maxPages || page == 0)
			page = maxPages;

		int start = 0;
		int end = 6;

		if (page != 1) {
			final int pageDifference = page - 1;

			// Because we need 7, not 6.
			start += 1;

			start += (6 * pageDifference);
			end = start + 6;
		}

		sender.sendMessage(ChatColor.GREEN + "-- Statz Commands --");

		for (int i = start; i < end; i++) {
			// Can't go any further
			if (i >= listSize)
				break;

			final StatzCommand command = commands.get(i);

			sender.sendMessage(ChatColor.AQUA + command.getUsage() + ChatColor.GRAY + " - " + command.getDescription());
		}

		sender.sendMessage(Lang.PAGE_INDEX.getConfigValue(page, maxPages));
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
