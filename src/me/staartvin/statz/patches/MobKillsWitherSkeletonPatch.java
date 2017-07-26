package me.staartvin.statz.patches;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;

import java.util.Arrays;
import java.util.List;

public class MobKillsWitherSkeletonPatch extends Patch {


    // Patch that Wither skeletons appear as 'WITHER WITHER_SKELETON'.
    // Also patch that Elder guardians appear as 'ELDER ELDER_GUARDIAN'

    public MobKillsWitherSkeletonPatch(Statz plugin) {
        super(plugin);
    }

    @Override
    public boolean applyMySQLChanges() {

        String tableName = this.getDatabaseConnector().getTable(PlayerStat.KILLS_MOBS).getTableName();

        List<String> queries = Arrays.asList(new String[] {
                "UPDATE " + tableName + " SET mob = 'WITHER_SKELETON' WHERE mob LIKE '%WITHER_SKELETON%'",
                "UPDATE " + tableName + " SET mob = 'ELDER_GUARDIAN' WHERE mob LIKE '%ELDER_GUARDIAN%'"});

        try {
            this.getDatabaseConnector().sendQueries(queries);

            return true;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.getStatz().getLogger().warning("Failed to patch MySQL database for patch " + this.getPatchId());
            return false;
        }

    }

    @Override
    public String getPatchName() {
        return "Wither Skeleton and Elder Guardian renaming - Mob Kills";
    }

    @Override
    public int getPatchId() {
        return 2;
    }

    @Override
    public boolean applySQLiteChanges() {
        String tableName = this.getDatabaseConnector().getTable(PlayerStat.KILLS_MOBS).getTableName();

        List<String> queries = Arrays.asList(new String[] {
                "UPDATE " + tableName + " SET mob = 'WITHER_SKELETON' WHERE mob LIKE '%WITHER_SKELETON%'",
                "UPDATE " + tableName + " SET mob = 'ELDER_GUARDIAN' WHERE mob LIKE '%ELDER_GUARDIAN%'" });

        try {
            this.getDatabaseConnector().sendQueries(queries);

            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.getStatz().getLogger().warning("Failed to patch SQLite database for patch " + this.getPatchId());
            return false;
        }

    }

}
