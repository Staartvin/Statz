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
        patches.add(new RemoveForceShotArrowsPatch(plugin));
    }

    public void applyPatches() {
        // Apply all patches in the correct order

        int count = 0;

        plugin.getLogger().info("---------- [Applying Database patches] ----------");

        boolean useMySQL = plugin.getConfigHandler().isMySQLEnabled();

        boolean noErrors = true;

        for (Patch patch : patches) {

            // We already applied this patch, so ignore it
            if (useMySQL && plugin.getConfigHandler().getLatestPatchMySQLVersion() >= patch.getPatchId()) {
                continue;
            } else if (!useMySQL && plugin.getConfigHandler().getLatestPatchSQLiteVersion() >= patch.getPatchId()) {
                continue;
            }

            // Don't apply the patch if it is not needed.
            if (!patch.isPatchNeeded()) {

                // Check to see if the current patch version is smaller than we just checked.
                // If it is, we update it to current patch version.
                if (plugin.getConfigHandler().isMySQLEnabled() && plugin.getConfigHandler().getLatestPatchMySQLVersion() < patch.getPatchId()) {
                    plugin.getConfigHandler().setLatestPatchMySQLVersion(patch.getPatchId());
                } else {
                    if (plugin.getConfigHandler().getLatestPatchSQLiteVersion() < patch.getPatchId()) {
                        plugin.getConfigHandler().setLatestPatchSQLiteVersion(patch.getPatchId());
                    }
                }

                continue;
            }

            // Before we start patching, make a backup of the data storage.
            if (this.createBackupDataStorage(patch)) {
                plugin.getLogger().info("Made backup of data storage before patching so you can roll back if " +
                        "something " +
                        "went wrong.");
            } else {
                // Don't patch if no backup could be made.
                plugin.getLogger().severe("Could not make a backup of data storage before patching, so will not " +
                        "continue patching.");
                noErrors = false;
                break;
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
                    success = false;
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
                plugin.getLogger().info("Consider rolling back this patch by looking into the backups of your data " +
                        "storage that were automatically created by Statz!");
                plugin.getLogsManager().writeToLogFile("Failed to apply patch '" + patch.getPatchName() + "' (id: " +
                        patch.getPatchId() + ").");
                plugin.getLogsManager().writeToLogFile("Consider rolling back this patch by looking into the backups " +
                        "of your data " +
                        "storage that were automatically created by Statz!");
                noErrors = false;
                break;
            }

            count++;
        }

        if (count == 0) {

            if (noErrors) {
                plugin.getLogger().info("---------- [No patches were applied! Database is already up-to-date.] " +
                        "----------");
            } else {
                plugin.getLogger().info("---------- [No patches have been applied, as one failed.] ----------");
            }
        } else {

            if (noErrors) {
                plugin.getLogger().info("---------- [" + count + " patches have been applied!] ----------");
            } else {
                plugin.getLogger().info("---------- [" + count + " patches have been applied, but not all of them " +
                        "were successful!] ----------");
            }


        }

    }

    /**
     * Create a backup of the storage of data. This is used to be able to rollback data when a patch has gone wrong.
     *
     * @param patch Patch that will be applied and the data storage has to be backed up for.
     * @return true if the storage was successfully backed up. False otherwise.
     */
    public boolean createBackupDataStorage(Patch patch) {
        return plugin.getDatabaseConnector().createBackup("patch_" + patch.getPatchId());
    }

}
