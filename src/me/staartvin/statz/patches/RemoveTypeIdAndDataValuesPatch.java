package me.staartvin.statz.patches;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.DatabaseConnector;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This patch updates the database schemas so Statz records the material names used in Minecraft 1.13 instead of the
 * deprecated type id and data values.
 */
public class RemoveTypeIdAndDataValuesPatch extends Patch {

    public RemoveTypeIdAndDataValuesPatch(Statz plugin) {
        super(plugin);
    }

    @Override
    public boolean applyMySQLChanges() {

        List<String> queries = convertMySQLTable(DatabaseConnector.getTable(PlayerStat.BLOCKS_PLACED).getTableName());

        queries.addAll(convertMySQLTable(DatabaseConnector.getTable(PlayerStat.BLOCKS_BROKEN).getTableName()));

        try {
            this.getDatabaseConnector().sendQueries(queries, false);

            return true;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.getStatz().getLogger().warning("Failed to patch MySQL database for patch " + this.getPatchId());
            return false;
        }

    }

    @Override
    public String getPatchName() {
        return "Remove type id and datavalue of materials";
    }

    @Override
    public int getPatchId() {
        return 4;
    }

    @Override
    public boolean applySQLiteChanges() {

        List<String> queries = convertSQLiteTable(DatabaseConnector.getTable(PlayerStat.BLOCKS_BROKEN)
                .getTableName(), DatabaseConnector.getTable(PlayerStat.BLOCKS_BROKEN)
                .getTableName() + "_temp");

        queries.addAll(convertSQLiteTable(DatabaseConnector.getTable(PlayerStat.BLOCKS_PLACED).getTableName(),
                DatabaseConnector.getTable(PlayerStat.BLOCKS_PLACED).getTableName() + "_temp"));

        try {
            this.getDatabaseConnector().sendQueries(queries, false);

            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.getStatz().getLogger().warning("Failed to patch SQLite database for patch " + this.getPatchId());
            return false;
        }

    }

    private List<String> convertSQLiteTable(String tableName, String temporaryName) {
        // Create new table with new indexes and column 'block' (without columns 'typeid' and 'datavalue').
        // Insert data from old table to new table (but convert to material name)
        // Drop old table
        // Rename new table to name of old table
        // Done!

        List<String> queries = new ArrayList<>();

        queries.add("CREATE TABLE " + temporaryName + " ('id' INTEGER PRIMARY KEY " +
                "NOT NULL, " +
                "'uuid' TEXT NOT NULL, 'value' INTEGER NOT NULL, 'world' TEXT NOT NULL, 'block' TEXT NOT NULL, UNIQUE" +
                " (uuid, block, world))");

        // Get all data in the table
        List<Query> data = this.getDatabaseConnector().getObjects(tableName);

        for (Query query : data) {

            int typeId = Integer.parseInt(query.getValue("typeid").toString());
            int dataValue = Integer.parseInt(query.getValue("datavalue").toString());

            org.bukkit.Material matchingMaterial = StatzUtil.findMaterial(typeId, dataValue);

            queries.add(String.format("INSERT INTO %s (uuid, value, world, block) VALUES ('%s', %s, '%s', '%s')",
                    temporaryName,
                    query.getUUID(), query.getValue(), query.getValue("world").toString(), matchingMaterial.name()));
        }

        queries.add("DROP TABLE IF EXISTS " + tableName);
        queries.add("ALTER TABLE " + temporaryName + " RENAME TO " + tableName);

        return queries;
    }

    private List<String> convertMySQLTable(String tableName) {
        List<String> queries = new ArrayList<>();

        // Add a new column called 'block'
        queries.add("ALTER TABLE " + tableName + " ADD block VARCHAR(100) NOT NULL");

        // Get all data in the table
        List<Query> data = this.getDatabaseConnector().getObjects(tableName);

        // Add value for each row for the new block column
        for (Query query : data) {

            int itemId = Integer.parseInt(query.getValue("typeid").toString());
            int damageValue = Integer.parseInt(query.getValue("datavalue").toString());

            queries.add("UPDATE " + tableName + " SET block='" + StatzUtil.findMaterial(itemId, damageValue) + "' " +
                    "WHERE id=" + query.getValue("id"));
        }

        // Update the index with the new column
        queries.add("ALTER TABLE " + tableName + " DROP INDEX uuid, ADD UNIQUE KEY `uuid` (`uuid`,`block`,`world`)");

        // Remove the old columns
        queries.add("ALTER TABLE " + tableName + " DROP COLUMN typeid, DROP COLUMN datavalue");


        return queries;
    }

}
