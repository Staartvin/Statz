package me.staartvin.statz.patches;

import me.staartvin.statz.Statz;

import java.util.ArrayList;
import java.util.List;

public class PatchManager {

    private Statz plugin;
    private List<Patch> patches = new ArrayList<>();

    public PatchManager(Statz plugin) {
        this.plugin = plugin;

        // Register patches.
        patches.add(new WeaponColumnMobKillsPatch(plugin));
        patches.add(new RenameWitherSkeletonPatch(plugin));
        patches.add(new RenameElderGuardianPatch(plugin));
        patches.add(new RemoveTypeIdAndDataValuesPatch(plugin));
        patches.add(new RenameFoodNamesPatch(plugin));
    }

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

                try {
                    success = patch.applyMySQLChanges();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (success) {
                    plugin.getConfigHandler().setLatestPatchMySQLVersion(patch.getPatchId());
                }
            } else {
                try {
                    success = patch.applySQLiteChanges();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (success) {
                    plugin.getConfigHandler().setLatestPatchSQLiteVersion(patch.getPatchId());
                }
            }

            if (success) {
                plugin.getLogger().info("Successfully applied patch '" + patch.getPatchName() + "' (id: " + patch
                        .getPatchId() + ").");
                plugin.getLogsManager().writeToLogFile("Successfully applied patch '" + patch.getPatchName() + "' " +
                        "(id: " + patch
                        .getPatchId()
                        + ").");
            } else {
                plugin.getLogger().info("Failed to apply patch '" + patch.getPatchName() + "' (id: " + patch
                        .getPatchId() + ").");
                plugin.getLogsManager().writeToLogFile("Failed to apply patch '" + patch.getPatchName() + "' (id: " +
                        patch
                                .getPatchId() + ").");
                break;
            }

            count++;
        }

        if (count == 0) {
            plugin.getLogger().info("---------- [No patches were applied! Database is already up-to-date.] ----------");
        } else {
            plugin.getLogger().info("---------- [" + count + " patches have been applied!] ----------");
        }

    }

}
