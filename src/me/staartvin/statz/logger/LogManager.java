package me.staartvin.statz.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.PlayerStat;
import net.md_5.bungee.api.ChatColor;

public class LogManager {

	private final static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
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
	 */
	public String createLogFile() {
		dateFormatSave = dateFormat.format(new Date());
		
		// Creates a new file
		logFile = new File(plugin.getDataFolder() + "/logs", "log-" + dateFormatSave + ".txt");

		try {
			logFile.getParentFile().mkdirs();
			logFile.createNewFile();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateFormatSave;
		}

		//Create our writer
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(logFile, true));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return dateFormatSave;
		}
	}

	public void writeToLogFile(List<Query> queries, PlayerStat stat) {

		// Creates a new file
		logFile = new File(plugin.getDataFolder() + "/logs", "log-" + dateFormatSave + ".txt");

		//Create our writer
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(logFile, true));
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		try {

			for (Query query : queries) {
				out.write("[PLAYERSTAT: " + stat + "] " + query.getLogString());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

}
