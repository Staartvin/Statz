package me.staartvin.statz.commands;

import me.staartvin.statz.Statz;
import me.staartvin.statz.commands.manager.StatzCommand;
import me.staartvin.statz.database.DatabaseConnector;
import me.staartvin.statz.database.MySQLConnector;
import me.staartvin.statz.database.SQLiteConnector;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.Table;
import me.staartvin.statz.datamanager.PlayerStat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TransferCommand extends StatzCommand {

	private static Statz plugin;
	private static DatabaseConnector SQLiteConnector, MySQLConnector;

	public TransferCommand(final Statz instance) {
		this.setUsage("/statz transfer");
		this.setDesc("Transfer Statz's SQLite database to MySQL database");
		this.setPermission("statz.transfer.sqlite");

		plugin = instance;
	}

	// Transfer from SQLite to MySQL
	public static List<String> confirmTransferSQLite = new ArrayList<>();

	// Transfer from MySQL to SQLite
	public static List<String> confirmTransferMySQL = new ArrayList<>();

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

		if (args.length > 1 && args[1].equalsIgnoreCase("reverse")) {
			// Run MySQL -> SQLite conversion

			if (confirmTransferMySQL.contains(sender.getName())) {
				sender.sendMessage(ChatColor.GREEN + "Please confirm this command with typing '" + ChatColor.GRAY
						+ "yes" + ChatColor.GREEN + "'!");
				return true;
			}

			// Check if SQLite is enabled.
			if (plugin.getConfigHandler().isMySQLEnabled()) {
				sender.sendMessage(ChatColor.RED + "SQLite should be enabled before running transfering!");
				return true;
			}

			sender.sendMessage(ChatColor.YELLOW
					+ "Performing this command will transfer your MySQL database to your SQLite database.");

			// If sender is not a player, don't ask for confirmation
			if (!(sender instanceof Player)) {
				// Don't ask for confirmation
				confirmReverseTransfer(sender);
				return true;
			}

			sender.sendMessage(ChatColor.DARK_AQUA + "Are you sure you want to do this? Type " + ChatColor.GOLD + "yes"
					+ ChatColor.DARK_AQUA + " to confirm this command.");

			confirmTransferMySQL.add(sender.getName());

			plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					// Player already confirmed or it was removed from the list
					if (!confirmTransferMySQL.contains(sender.getName()))
						return;

					confirmTransferMySQL.remove(sender.getName());

					sender.sendMessage(ChatColor.RED + "Confirmation of " + ChatColor.GRAY + "/statz transfer reverse"
							+ ChatColor.RED + " command expired!");
				}

			}, 20 * 10);

		} else {
			// Run SQLite -> MySQL conversion

			if (confirmTransferSQLite.contains(sender.getName())) {
				sender.sendMessage(ChatColor.GREEN + "Please confirm this command with typing '" + ChatColor.GRAY
						+ "yes" + ChatColor.GREEN + "'!");
				return true;
			}

			// Check if MySQL is enabled.
			if (!plugin.getConfigHandler().isMySQLEnabled()) {
				sender.sendMessage(ChatColor.RED + "MySQL should be enabled before running transfering!");
				return true;
			}

			sender.sendMessage(ChatColor.YELLOW
					+ "Performing this command will transfer your SQLite database to your MySQL database.");

			// If sender is not a player, don't ask for confirmation
			if (!(sender instanceof Player)) {
				// Don't ask for confirmation
				confirmTransfer(sender);
				return true;
			}

			sender.sendMessage(ChatColor.DARK_AQUA + "Are you sure you want to do this? Type " + ChatColor.GOLD + "yes"
					+ ChatColor.DARK_AQUA + " to confirm this command.");

			confirmTransferSQLite.add(sender.getName());

			plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					// Player already confirmed or it was removed from the list
					if (!confirmTransferSQLite.contains(sender.getName()))
						return;

					confirmTransferSQLite.remove(sender.getName());

					sender.sendMessage(ChatColor.RED + "Confirmation of " + ChatColor.GRAY + "/statz transfer"
							+ ChatColor.RED + " command expired!");
				}

			}, 20 * 10);
		}

		return true;
	}

	public static void confirmTransfer(final CommandSender sender) {
		// Run after a player has confirmed the command.

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			public void run() {
				confirmTransferSQLite.remove(sender.getName());
				
				sender.sendMessage(ChatColor.GOLD + "Transferring database records... This make take a while!");
				
				// Load SQLiteConnector
				SQLiteConnector = new SQLiteConnector(plugin);

				SQLiteConnector.loadTables();
				SQLiteConnector.load();

				int updateCount = 0;
				
				plugin.getLogsManager().writeToLogFile("Starting tranfer from SQLite to MySQL database!");

				for (PlayerStat stat : PlayerStat.values()) {
					// When using null as queries parameter, it will get all data in the table.
					List<Query> storedSQLiteQueries = SQLiteConnector.getObjects(stat.getTableName(), null);

					Table table = plugin.getDatabaseConnector().getTable(stat.getTableName());
					
					// Write transferred items to log
					plugin.getLogsManager().writeToLogFile(storedSQLiteQueries, stat);
					
					plugin.getDatabaseConnector().setBatchObjects(table, storedSQLiteQueries, 2);

					updateCount += storedSQLiteQueries.size();
				}
				
				plugin.getLogsManager().writeToLogFile("Wrote " + updateCount + " changes while transferring SQLite to MySQL database");

				sender.sendMessage(ChatColor.GREEN + "Transferred " + ChatColor.GOLD + updateCount + ChatColor.GREEN
						+ " database records from SQLite to MySQL!");
			}
		});

	}

	public static void confirmReverseTransfer(final CommandSender sender) {
		// Run after a player has confirmed the command.

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

			public void run() {
				confirmTransferMySQL.remove(sender.getName());
				
				sender.sendMessage(ChatColor.GOLD + "Transferring database records... This make take a while!");
				
				// Load SQLiteConnector
				MySQLConnector = new MySQLConnector(plugin);

				MySQLConnector.loadTables();
				MySQLConnector.load();

				int updateCount = 0;
				
				plugin.getLogsManager().writeToLogFile("Starting tranfer from MySQL to SQLite database!");

				for (PlayerStat stat : PlayerStat.values()) {
					// When using null as queries parameter, it will get all data in the table.
					List<Query> storedMySQLQueries = MySQLConnector.getObjects(stat.getTableName(), null);

					// Remove ID column because SQLite automatically assigns id's to its tables.
					for (Query q: storedMySQLQueries) {
						if (q.hasKey("id")) {
							q.removeColumn("id");
						}
					}
					
					// Write transferred items to log
					plugin.getLogsManager().writeToLogFile(storedMySQLQueries, stat);
					
					Table table = plugin.getDatabaseConnector().getTable(stat.getTableName());

					plugin.getDatabaseConnector().setBatchObjects(table, storedMySQLQueries, 2);

					updateCount += storedMySQLQueries.size();
				}
				
				plugin.getLogsManager().writeToLogFile("Wrote " + updateCount + " changes while transferring MySQL to SQLite database");

				sender.sendMessage(ChatColor.GREEN + "Transferred " + ChatColor.GOLD + updateCount + ChatColor.GREEN
						+ " database records from MySQL to SQLite!");

				
			}
		});

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
