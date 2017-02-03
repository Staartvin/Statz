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

        boolean useMySQL = plugin.getConfigHandler().isMySQLEnabled();

        for (Patch patch : patches) {

            // We already applied this patch, so ignore it
            if (useMySQL && plugin.getConfigHandler().getLatestPatchMySQLVersion() >= patch.getPatchId()) {
                continue;
            } else if (!useMySQL && plugin.getConfigHandler().getLatestPatchSQLiteVersion() >= patch.getPatchId()) {
                continue;
            }

            plugin.getLogger().info("Applying patch '" + patch.getPatchName() + "' (id: " + patch.getPatchId() + ").");

            // Update latest patch information.
            if (plugin.getConfigHandler().isMySQLEnabled()) {
                if (patch.applyMySQLChanges()) {
                    plugin.getConfigHandler().setLatestPatchMySQLVersion(patch.getPatchId());
                } else {
                    // Failed
                }
            } else {
                if (patch.applySQLiteChanges()) {
                    plugin.getConfigHandler().setLatestPatchSQLiteVersion(patch.getPatchId());
                } else {
                 // Failed
                }
            }

            count++;
        }

        plugin.getLogger().info("---------- [" + count + " patches have been applied!] ----------");
    }

}
