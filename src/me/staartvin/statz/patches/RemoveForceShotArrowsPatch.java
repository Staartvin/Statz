package me.staartvin.statz.patches;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerStat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class RemoveForceShotArrowsPatch extends Patch {

    public RemoveForceShotArrowsPatch(Statz plugin) {
        super(plugin);
    }

    @Override
    public boolean applyMySQLChanges() {

        String tableName = this.getDatabaseConnector().getTable(PlayerStat.ARROWS_SHOT).getTableName();
        String tempName = tableName + "_temp";

        // AIM: We remove the forceShot column from the table to make the data less dense.

        // Create temporary table to get data from
        // Fill temporary table with old data
        // Drop old table
        // Create table that will replace old table without the forceShot column
        // Move data from temporary table to new table (but without forceShot data)
        // Remove temp table
        // Done! :-)

        List<String> queries = Arrays.asList(
                "CREATE TABLE " + tempName + " AS (SELECT uuid, value, world, forceShot FROM " + tableName + ");",
                "DROP TABLE " + tableName + ";",
                "CREATE TABLE " + tableName + " (`id` bigint(20) NOT NULL, `uuid` varchar(100) NOT NULL, `value` " +
                        "bigint(20) NOT NULL, " +
                        "`world` varchar(100) NOT NULL);",
                "ALTER TABLE " + tableName + " ADD PRIMARY KEY (id), ADD UNIQUE KEY uuid (uuid, world);",
                "ALTER TABLE " + tableName + " MODIFY id bigint(20) NOT NULL AUTO_INCREMENT;",
                "INSERT INTO " + tableName + " (SELECT null as id, uuid, SUM(value), world FROM " + tempName +
                        " GROUP BY uuid, world);",
                "DROP TABLE " + tempName + ";"
        );

        try {
            this.getDatabaseConnector().sendQueries(queries, false);

            return true;
        } catch (Exception e) {

            this.getStatz().getLogger().warning("Failed to patch MySQL database for patch " + this.getPatchId());
            return false;
        }

    }

    @Override
    public String getPatchName() {
        return "Remove forceShot column - Arrows shot";
    }

    @Override
    public int getPatchId() {
        return 6;
    }

    @Override
    public boolean isPatchNeeded() {
        String query;
        String tableName = this.getDatabaseConnector().getTable(PlayerStat.ARROWS_SHOT).getTableName();

        // The query is different whether it's MySQL or SQLite
        if (this.plugin.getConfigHandler().isMySQLEnabled()) {
            query = "SHOW COLUMNS FROM " + tableName;
        } else {
            query = "PRAGMA table_info(" + tableName + ")";
        }

        try (ResultSet resultSet =
                     this.getDatabaseConnector().sendQuery(query, true)) {

            while (resultSet != null && resultSet.next()) {
                // If there is column called 'forceShot', we DO have to patch
                if (resultSet.getString(this.plugin.getConfigHandler().isMySQLEnabled() ? 1 : 2).equalsIgnoreCase(
                        "forceShot")) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }

        // The column 'forceShot' was not found, so we don't need to patch.
        return false;
    }

    @Override
    public boolean applySQLiteChanges() {
        String tableName = this.getDatabaseConnector().getTable(PlayerStat.ARROWS_SHOT).getTableName();
        String tempName = tableName + "_temp";

        // AIM: We remove the forceShot column from the table to make the data less dense.

        // Create temporary table to get data from
        // Fill temporary table with old data
        // Drop old table
        // Create table that will replace old table without the forceShot column
        // Move data from temporary table to new table (but without forceShot data)
        // Remove temp table
        // Done! :-)

        List<String> queries = Arrays.asList(
                "CREATE TEMPORARY TABLE " + tempName + "(uuid, value, world, forceShot);",
                "INSERT INTO " + tempName + " SELECT uuid, value, world, forceShot FROM " + tableName + ";",
                "DROP TABLE " + tableName + ";",
                "CREATE TABLE " + tableName + " (id INTEGER NOT NULL, uuid TEXT NOT NULL, value INTEGER NOT " +
                        "NULL, " +
                        "world TEXT NOT NULL, PRIMARY KEY(\"id\"), UNIQUE(\"uuid\", \"world\"));",
                "INSERT INTO " + tableName + " SELECT null as id, uuid, SUM(value), world FROM " + tempName + " GROUP" +
                        " BY uuid, " +
                        "world;",
                "DROP TABLE " + tempName + ";"
        );

        try {
            this.getDatabaseConnector().sendQueries(queries, false);

            return true;
        } catch (Exception e) {

            this.getStatz().getLogger().warning("Failed to patch SQLite database for patch " + this.getPatchId());
            return false;
        }

    }

}
