package me.staartvin.statz.logger;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.player.PlayerStat;
import org.bukkit.ChatColor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This class handles all the data that should be written to a log file.
 * @author Staartvin
 *
 */
public class LogManager {

	private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private final static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	private final static DateFormat humanDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String dateFormatSave;

	private final Statz plugin;

	private File logFile;

	public LogManager(final Statz instance) {
		plugin = instance;
	}

	/**
	 * Creates a log file.
	 * <br>
	 * The file will be created in the folder 'debugger' and will have the name
	 * 'debug-yyyyMMddHHmmss'.
	 * @return The format date that will be used to create this log file.
	 */
	public String createLogFile() {
		dateFormatSave = dateFormat.format(new Date());

        // If logging is disabled, you cannot write to the logging file.
        if (!plugin.getConfigHandler().isLoggingEnabled()) return dateFormatSave;

		// Creates a new file
		logFile = new File(plugin.getDataFolder() + "/logs", "log-" + dateFormatSave + ".txt");

		try {
			logFile.getParentFile().mkdirs();
			logFile.createNewFile();
		} catch (final IOException e) {

            e.printStackTrace();
			return dateFormatSave;
		}

		//Create our writer
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(logFile, true));
		} catch (final IOException e) {

            e.printStackTrace();
			return dateFormatSave;
		}

		try {
			out.write("This is a log file of Statz made on " + humanDateFormat.format(new Date()));
			out.newLine();
			out.write("");
			out.newLine();
		} catch (Exception e) {
			plugin.debugMessage(ChatColor.RED + "Error when writing to log file!");
		}

		//close
		try {
			out.close();
			return dateFormatSave;
		} catch (final IOException e) {

            e.printStackTrace();
			return dateFormatSave;
		}
	}

	/**
	 * Write query objects of a given statistic to a log file.
	 * <br>This method will format the queries into human-readable form to write into the log file.
	 * @param queries Queries to write to log file
	 * @param stat Statistic that the given queries correspond to. 
	 */
	public void writeToLogFile(List<Query> queries, PlayerStat stat) {

        // If logging is disabled, you cannot write to the logging file.
        if (!plugin.getConfigHandler().isLoggingEnabled()) return;

		// Creates a new file
		logFile = new File(plugin.getDataFolder() + "/logs", "log-" + dateFormatSave + ".txt");

		//Create our writer
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(logFile, true));
		} catch (final IOException e) {

            e.printStackTrace();
			return;
		}

		try {

			for (Query query : queries) {
				out.write("[" + timeFormat.format(new Date()) + "] [PLAYERSTAT: " + stat + "] " + query.getLogString());
				out.newLine();
			}

		} catch (Exception e) {
			plugin.debugMessage(ChatColor.RED + "Error when writing to log file!");
		}

		//close
		try {
			out.close();
			return;
		} catch (final IOException e) {

            e.printStackTrace();
			return;
		}
	}

	/**
	 * Write a single message to a log file
	 * @param message Message to write to log file.
	 */
	public void writeToLogFile(String message) {

        // If logging is disabled, you cannot write to the logging file.
        if (!plugin.getConfigHandler().isLoggingEnabled()) return;

		// Creates a new file
		logFile = new File(plugin.getDataFolder() + "/logs", "log-" + dateFormatSave + ".txt");

		//Create our writer
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(logFile, true));
		} catch (final IOException e) {

            e.printStackTrace();
			return;
		}

		try {

			out.write("[" + timeFormat.format(new Date()) + "] " + message);
			out.newLine();

		} catch (Exception e) {
			plugin.debugMessage(ChatColor.RED + "Error when writing to log file!");
		}

		//close
		try {
			out.close();
			return;
		} catch (final IOException e) {

            e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Write multiple messages to a log file.
	 * @param messages List of messages to write to log file.
	 */
	public void writeToLogFile(List<String> messages) {

        // If logging is disabled, you cannot write to the logging file.
        if (!plugin.getConfigHandler().isLoggingEnabled()) return;

		// Creates a new file
		logFile = new File(plugin.getDataFolder() + "/logs", "log-" + dateFormatSave + ".txt");

		//Create our writer
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(logFile, true));
		} catch (final IOException e) {

            e.printStackTrace();
			return;
		}

		try {

			for (String message: messages){
				out.write("[" + timeFormat.format(new Date()) + "] " + message);
				out.newLine();
			}	

		} catch (Exception e) {
			plugin.debugMessage(ChatColor.RED + "Error when writing to log file!");
		}

		//close
		try {
			out.close();
			return;
		} catch (final IOException e) {

            e.printStackTrace();
			return;
		}
	}

}
