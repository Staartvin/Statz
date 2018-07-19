package me.staartvin.statz.language;

import me.staartvin.statz.Statz;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Class used to reload/create the language file of Statz.
 * @author Staartvin 
 */
public class LanguageHandler {

	private FileConfiguration languageConfig;
	private File languageConfigFile;
	private final Statz plugin;

	public LanguageHandler(final Statz plugin) {
		this.plugin = plugin;
	}

	public void createNewFile() {
		reloadConfig();
		saveConfig();

		Lang.setFile(languageConfig);
		
		loadConfig();

		plugin.getLogger().info("Language file loaded (lang.yml)");
	}

	public FileConfiguration getConfig() {
		if (languageConfig == null) {
			this.reloadConfig();
		}
		return languageConfig;
	}

	public void loadConfig() {

		languageConfig.options().header("Language file");

		for (final Lang value : Lang.values()) {
			languageConfig.addDefault(value.getPath(), value.getDefault());
		}

		languageConfig.options().copyDefaults(true);
		saveConfig();
	}

	public void reloadConfig() {
		if (languageConfigFile == null) {
			languageConfigFile = new File(plugin.getDataFolder() + "/lang", "lang.yml");
		}
		languageConfig = YamlConfiguration.loadConfiguration(languageConfigFile);
	}

	public void saveConfig() {
		if (languageConfig == null || languageConfigFile == null) {
			return;
		}
		try {
			getConfig().save(languageConfigFile);
		} catch (final IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + languageConfigFile, ex);
		}
	}
}
