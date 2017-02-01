package me.staartvin.statz.language;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Every enumeration value has its path and default value.
 * To get the path, do {@link #getPath()}.
 * To get the default value, do {@link #getDefault()}.
 * 
 * For the defined value in the lang.yml config, use
 * {@link #getConfigValue(Object...)}.
 * String objects are expected as input.
 * 
 * @author Staartvin and gomeow
 * 
 */
public enum Lang {
	/**
	 * &4You did not provide a correct page number!
	 */
	INCORRECT_PAGE_NUMBER("incorrect-page-number", "&4You did not provide a correct page number!"),
	/**
	 * &9Page {0} of {1}
	 */
	PAGE_INDEX("page-index", "&9Page {0} of {1}"),
	/**
	 * &4This command can only be performed by players!
	 */
	COMMAND_PERFORMED_ONLY_PLAYERS("command-performed-only-players", "&4This command can only be performed by players!"),
	/**
	 * &4{0} has never played on this server before!
	 */
	PLAYER_NEVER_PLAYED_BEFORE("player-never-played-before", "&4{0} has never played on this server before!"),
	/**
	 * &4There is nothing to show for this stat.
	 */
	NO_STATISTICS_TO_SHOW("no-statistics-to-show", "&4There is nothing to show for this stat."),
	/**
	 * &4{0} is not a correct stat!
	 */
	INCORRECT_STAT_TYPE("incorrect-stat-type", "&4{0} is not a correct stat!"),
	/**
	 * &e---- [Stat {0} of {1}] ----
	 */
	SPECIFIC_STAT_HEADER("specific-stat-header", "&e---- [Stat {0} of {1}] ----"),
	/**
	 * &eStatz is currently hooked and listening to the following plugins:
	 */
	STATZ_HOOKED_AND_LISTENING("statz-hooked-and-listening", "&eStatz is currently hooked and listening to the following plugins:"),
	/**
	 * &4Statz could not detect {0} running on this server!
	 */
	DID_NOT_FIND_DEPENDENCY("did-not-find-dependency", "&4Statz could not detect {0} running on this server!"),
	/**
	 * &4This command was not recognized!
	 */
	COMMAND_NOT_RECOGNIZED("command-not-recognized", "&4This command was not recognized!"),
	/**
	 * &eUse '/statz help' for a list of commands.
	 */
	STATZ_HELP_COMMAND("statz-help-command", "&eUse '/statz help' for a list of commands."),
	
	/**
	 * &4You do not have the permission ({0}) to perform this command."
	 */
	INSUFFICIENT_PERMISSIONS("insufficient-permissions", "&4You do not have the permission ({0}) to perform this command."),
	
	/**
	 * &4Incorrect command usage! Try &e{0}
	 */
	INCORRECT_COMMAND_USAGE("incorrect-command-usage", "&4Incorrect command usage! Try &e{0}");

	private static FileConfiguration LANG;

	/**
	 * Set the {@code FileConfiguration} to use.
	 * 
	 * @param config The config to set.
	 */
	public static void setFile(final FileConfiguration config) {
		LANG = config;
	}

	private String path, def;

	/**
	 * Lang enum constructor.
	 * 
	 * @param path The string path.
	 * @param start The default string.
	 */
	Lang(final String path, final String start) {
		this.path = path;
		this.def = start;
	}

	/**
	 * Get the value in the config with certain arguments.
	 * 
	 * @param args arguments that need to be given. (Can be null)
	 * @return value in config or otherwise default value
	 */
	public String getConfigValue(final Object... args) {
		String value = ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));

		if (args == null)
			return value;
		else {
			if (args.length == 0)
				return value;

			for (int i = 0; i < args.length; i++) {
				value = value.replace("{" + i + "}", args[i].toString());
			}
		}

		return value;
	}

	/**
	 * Get the default value of the path.
	 * 
	 * @return The default value of the path.
	 */
	public String getDefault() {
		return this.def;
	}

	/**
	 * Get the path to the string.
	 * 
	 * @return The path to the string.
	 */
	public String getPath() {
		return this.path;
	}
}
