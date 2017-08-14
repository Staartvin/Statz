package me.staartvin.statz.patches;

import java.util.ArrayList;
import java.util.List;

import me.staartvin.statz.Statz;

public class PatchManager {

    private Statz plugin;

    public PatchManager(Statz plugin) {
        this.plugin = plugin;

        // Register patches.
        patches.add(new WeaponColumnMobKillsPatch(plugin));
        patches.add(new RenameWitherSkeletonPatch(plugin));
        patches.add(new RenameElderGuardianPatch(plugin));
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

            boolean success = false;

            // Update latest patch information.
            if (plugin.getConfigHandler().isMySQLEnabled()) {

                success = patch.applyMySQLChanges();

                if (success) {
                    plugin.getConfigHandler().setLatestPatchMySQLVersion(patch.getPatchId());
                }
            } else {
                success = patch.applySQLiteChanges();

                if (success) {
                    plugin.getConfigHandler().setLatestPatchSQLiteVersion(patch.getPatchId());
                }
            }

            if (success) {
                plugin.getLogger().info("Successfully applied patch '" + patch.getPatchName() + "' (id: " + patch.getPatchId() + ").");
            } else {
                plugin.getLogger().info("Failed to apply patch '" + patch.getPatchName() + "' (id: " + patch.getPatchId() + ").");
                break;
            }

            count++;
        }

        plugin.getLogger().info("---------- [" + count + " patches have been applied!] ----------");
    }

}
