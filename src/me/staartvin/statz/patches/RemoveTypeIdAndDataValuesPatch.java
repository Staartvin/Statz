package me.staartvin.statz.patches;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.DatabaseConnector;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

import java.util.Arrays;
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

        String tableName = this.getDatabaseConnector().getTable(PlayerStat.KILLS_MOBS).getTableName();

        List<String> queries = Arrays.asList("ALTER TABLE " + tableName + " ADD weapon VARCHAR(255) NOT NULL", "ALTER" +
                " TABLE " + tableName
                + " DROP INDEX `uuid`, ADD UNIQUE `uuid` (`uuid`, `mob`, `world`, `weapon`) USING BTREE;");

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
        return "Convert database to 1.13 Materials";
    }

    @Override
    public int getPatchId() {
        return 1;
    }

    @Override
    public boolean applySQLiteChanges() {

        List<String> queries = convertTable(DatabaseConnector.getTable(PlayerStat.BLOCKS_BROKEN)
                .getTableName(), DatabaseConnector.getTable(PlayerStat.BLOCKS_BROKEN)
                .getTableName() + "_temp");

        queries.addAll(convertTable(DatabaseConnector.getTable(PlayerStat.BLOCKS_PLACED).getTableName(),
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

    private List<String> convertTable(String tableName, String temporaryName) {
        // Create new table with new indexes and column 'block' (without columns 'typeid' and 'datavalue').
        // Insert data from old table to new table (but convert to material name)
        // Drop old table
        // Rename new table to name of old table
        // Done!

        List<String> queries = Arrays.asList("CREATE TABLE " + temporaryName + " ('id' INTEGER PRIMARY KEY NOT NULL, " +
                "'uuid' TEXT NOT NULL, 'value' INTEGER NOT NULL, 'world' TEXT NOT NULL, 'block' TEXT NOT NULL, UNIQUE" +
                " (uuid, block, world))");

        // Get all data in the table
        List<Query> data = this.getDatabaseConnector().getObjects(tableName);

        for (Query query : data) {

            int typeId = Integer.parseInt(query.getValue("typeid").toString());
            int dataValue = Integer.parseInt(query.getValue("datavalue").toString());

            org.bukkit.Material matchingMaterial = StatzUtil.findMaterial(typeId, dataValue);

            System.out.println(String.format("Found Material %s for item id %d, data value %d", matchingMaterial,
                    typeId, dataValue));

            queries.add(String.format("INSERT INTO %s (uuid, value, world, block) VALUES ('%s', %s, '%s', '%s')",
                    temporaryName,
                    query.getUUID(), query.getValue(), query.getValue("world").toString(), matchingMaterial.name()));
        }

        queries.add("DROP TABLE IF EXISTS " + tableName);
        queries.add("ALTER TABLE " + temporaryName + " RENAME TO " + tableName);

        return queries;
    }

}
