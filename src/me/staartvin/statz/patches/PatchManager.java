package me.staartvin.statz.patches;

import java.util.ArrayList;
import java.util.List;

import me.staartvin.statz.Statz;

public class PatchManager {

	private Statz plugin;

	public PatchManager(Statz plugin) {
		this.plugin = plugin;
		
		patches.add(new WeaponColumnMobKillsPatch(plugin));
	}
	
	private List<Patch> patches = new ArrayList<>();

	public void applyPatches() {
		// Apply all patches in the correct order

		int count = 0;
		
		plugin.getLogger().info("---------- [Applying Database patches] ----------");
		
		for (Patch patch : patches) {
			
			// We already applied this patch, so ignore it
			if (plugin.getConfigHandler().getLatestPatchVersion() >= patch.getPatchId()) continue;
			
			plugin.getLogger().info("Applying patch '" + patch.getPatchName() + "' (id: " + patch.getPatchId() + ").");
			
			if (plugin.getConfigHandler().isMySQLEnabled()) {
				patch.applyMySQLChanges();
			} else {
				patch.applySQLiteChanges();
			}
			
			count++;
		}
		
		plugin.getLogger().info("---------- [" + count + " patches have been applied] ----------");
	}
	
}
