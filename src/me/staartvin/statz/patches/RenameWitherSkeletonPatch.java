package me.staartvin.statz.patches;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenameWitherSkeletonPatch extends Patch {


    // Patch that Wither skeletons appear as 'WITHER WITHER_SKELETON'.
    // Also patch that Elder guardians appear as 'ELDER ELDER_GUARDIAN'

    public RenameWitherSkeletonPatch(Statz plugin) {
        super(plugin);
    }

    @Override
    public boolean applyMySQLChanges() {
        String tableName = this.getDatabaseConnector().getTable(PlayerStat.KILLS_MOBS).getTableName();
        String tempName = "tempKillsMobsTable";

        List<String> queries = Arrays.asList("CREATE TABLE " + tempName + " AS " +
                        "SELECT table1.id AS id, " +
                        "table1.uuid AS uuid, " +
                        "table1.value AS value, " +
                        "table1.world AS world, " +
                        "table1.mob AS mob, " +
                        "table1.weapon AS weapon, " +
                        "table2.id AS 'id:1', " +
                        "table2.uuid AS 'uuid:1', " +
                        "table2.value AS 'value:1', " +
                        "table2.world AS 'world:1', " +
                        "table2.mob AS 'mob:1', " +
                        "table2.weapon AS 'weapon:1'" +
                        " FROM " +
                        "(SELECT * FROM " + tableName + " WHERE mob='WITHER SKELETON') AS table1 " +
                        "INNER JOIN " +
                        "(SELECT * FROM " + tableName + " WHERE mob LIKE '%WITHER WITHER_SKELETON%') AS table2 ON " +
                        "table1.uuid = table2.uuid AND " +
                        "table1.world = table2.world AND " +
                        "table1.weapon = table2.weapon;",
                "SELECT * FROM " + tempName + ";");

        try {
            this.getDatabaseConnector().sendQuery(queries.get(0), false);
            ResultSet set = this.getDatabaseConnector().sendQuery(queries.get(1), true);

            if (set == null) {
                return false;
            }

            // Clear queries to add new ones.
            queries = new ArrayList<>();

            if (set.next()) {
                List<WitherTuple> tuples = new ArrayList<>();

                do {
                    int firstID = set.getInt("id");
                    int lastID = set.getInt("id:1");
                    int updatedValue = set.getInt("value") + set.getInt("value:1");

                    tuples.add(new WitherTuple(firstID, lastID, updatedValue));
                } while (set.next());

                for (WitherTuple tuple : tuples) {
                    String query = "UPDATE " + tableName + " SET value=" + tuple.updatedValue + " WHERE id=" + tuple.firstID;
                    String query2 = "DELETE FROM " + tableName + " WHERE id=" + tuple.lastID;

                    queries.add(query);
                    queries.add(query2);
                }
            }

            // Add query that updates mob names of rows that are unique (do not have another identical row).
            queries.add("UPDATE " + tableName + " SET mob='WITHER_SKELETON' WHERE mob='WITHER WITHER_SKELETON'");

            // Add query that updates mob names of rows that have 'WITHER SKELETON' to 'WITHER_SKELETON'.
            queries.add("UPDATE " + tableName + " SET mob='WITHER_SKELETON' WHERE mob='WITHER SKELETON'");

            // Remove temptable
            queries.add("DROP TABLE " + tempName);

            // Send the queries
            this.getDatabaseConnector().sendQueries(queries, false);

            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.getStatz().getLogger().warning("Failed to patch MySQL database for patch " + this.getPatchId());
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public String getPatchName() {
        return "Wither Skeleton renaming - Mob Kills";
    }

    @Override
    public int getPatchId() {
        return 2;
    }

    @Override
    public boolean applySQLiteChanges() {
        String tableName = this.getDatabaseConnector().getTable(PlayerStat.KILLS_MOBS).getTableName();
        String tempName = "tempKillsMobsTable";

        List<String> queries = Arrays.asList("CREATE TABLE " + tempName + " AS " +
                        "SELECT * FROM " +
                        "(SELECT * FROM " + tableName + " WHERE mob='WITHER SKELETON') AS table1 " +
                        "INNER JOIN " +
                        "(SELECT * FROM " + tableName + " WHERE mob LIKE '%WITHER WITHER_SKELETON%') AS table2 ON " +
                        "table1.uuid = table2.uuid AND " +
                        "table1.world = table2.world AND " +
                        "table1.weapon = table2.weapon;",
                "SELECT * FROM " + tempName + ";");

        try {
            this.getDatabaseConnector().sendQuery(queries.get(0), false);
            ResultSet set = this.getDatabaseConnector().sendQuery(queries.get(1), true);

            if (set == null) {
                return false;
            }

            // Clear queries to add new ones.
            queries = new ArrayList<>();

            if (set.next()) {
                List<WitherTuple> tuples = new ArrayList<>();

                do {
                    int firstID = set.getInt("id");
                    int lastID = set.getInt("id:1");
                    int updatedValue = set.getInt("value") + set.getInt("value:1");

                    tuples.add(new WitherTuple(firstID, lastID, updatedValue));
                } while (set.next());

                for (WitherTuple tuple : tuples) {
                    String query = "UPDATE " + tableName + " SET value=" + tuple.updatedValue + " WHERE id=" + tuple.firstID;
                    String query2 = "DELETE FROM " + tableName + " WHERE id=" + tuple.lastID;

                    queries.add(query);
                    queries.add(query2);
                }
            }

            // Add query that updates mob names of rows that are unique (do not have another identical row).
            queries.add("UPDATE " + tableName + " SET mob='WITHER_SKELETON' WHERE mob='WITHER WITHER_SKELETON'");

            // Add query that updates mob names of rows that have 'WITHER SKELETON' to 'WITHER_SKELETON'.
            queries.add("UPDATE " + tableName + " SET mob='WITHER_SKELETON' WHERE mob='WITHER SKELETON'");

            // Remove temptable
            queries.add("DROP TABLE " + tempName);

            // Send the queries
            this.getDatabaseConnector().sendQueries(queries, false);

            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.getStatz().getLogger().warning("Failed to patch SQLite database for patch " + this.getPatchId());
            e.printStackTrace();
            return false;
        }

    }

}

class WitherTuple {

    // First ID is the ID of the record with 'WITHER SKELETON'.
    // Last ID is the ID of the record with 'WITHER WITHER_SKELETON".
    // UpdatedValue is the value that the database needs to be updated to.
    protected int firstID, updatedValue, lastID;

    public WitherTuple(int firstID, int lastID, int updatedValue) {
        this.firstID = firstID;
        this.lastID = lastID;
        this.updatedValue = updatedValue;
    }
}
