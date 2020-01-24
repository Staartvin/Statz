package me.staartvin.statz.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Column;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.database.datatype.Table;
import me.staartvin.statz.database.datatype.Table.SQLDataType;
import me.staartvin.statz.database.datatype.mysql.MySQLTable;
import me.staartvin.statz.datamanager.player.PlayerStat;
import org.bukkit.ChatColor;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

public class MySQLConnector extends DatabaseConnector {

    private final Statz plugin;
    private String hostname = "localhost:3306";
    private String password = "";
    private String username = "root";

    private HikariDataSource dataSource = null;

    public MySQLConnector(final Statz instance) {
        super(instance);
        plugin = instance;

        // Load info from config
        this.loadMySQLInfo();
    }

    public void loadMySQLInfo() {
        hostname = plugin.getConfigHandler().getMySQLHostname();
        password = plugin.getConfigHandler().getMySQLPassword();
        username = plugin.getConfigHandler().getMySQLUsername();
        DatabaseConnector.databaseName = plugin.getConfigHandler().getMySQLDatabase();
    }

    private void setupDatabaseConnection() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + this.hostname + "/" + DatabaseConnector.databaseName);
        config.setUsername(this.username);
        config.setPassword(this.password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.addDataSourceProperty("leakDetectionThreshold", "10000");

        this.dataSource = new HikariDataSource(config);
    }

    /*
     * (non-Javadoc)
     *
     * @see me.staartvin.statz.database.Database#getSQLConnection()
     */
    @Override
    public Connection getConnection() {

        if (dataSource == null) {
            setupDatabaseConnection();
        }

        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see me.staartvin.statz.database.Database#load()
     */
    @Override
    public void load() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = getConnection()) {

                // Did not properly connect to database
                if (connection == null) {
                    plugin.debugMessage(ChatColor.RED + "I could not connect to your database! Are your credentials " +
                            "correct?");
                    return;
                }

                int existingTables = 0;


                final Statement s = connection.createStatement();

                // Run all statements to create tables
                for (final String statement : createTablesStatement()) {
                    s.executeUpdate(statement);
                }

                s.close();
            } catch (final SQLException e) {
                e.printStackTrace();
            }

            initialize();

            // Apply patches
            plugin.getPatchManager().applyPatches();
        });
    }

    @Override
    public List<Query> getObjects(Table table, RowRequirement... requirements) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        final List<Query> results = new ArrayList<>();

        if (table == null) {
            plugin.debugMessage("Tried to get data from a null table! This means some tables are not setup");
            return results;
        }

        try (Connection connection = getConnection()) {

            if (connection == null) {
                plugin.getLogger().warning("Statz is not connected to your database properly!");
                return new ArrayList<>();
            }

            // Create SQL query to retrieve data
            if (requirements == null || requirements.length == 0) {
                // No requirements, so we can grab all data in the table.
                ps = connection.prepareStatement("SELECT * FROM " + table.getTableName());
            } else {
                // We have requirements, so we need to filter the data using WHERE clause of SQL.
                StringBuilder builder = new StringBuilder(String.format("SELECT * FROM %s WHERE ", table.getTableName
                        ()));

                // Create a SQL WHERE string.
                for (int i = 0; i < requirements.length; i++) {
                    RowRequirement requirement = requirements[i];
                    if (i == requirements.length - 1) {
                        builder.append(String.format("%s = '%s';", requirement.getColumnName(), requirement
                                .getColumnValue()));
                    } else {
                        builder.append(String.format("%s = '%s' AND ", requirement.getColumnName(), requirement
                                .getColumnValue()));
                    }
                }

                ps = connection.prepareStatement(builder.toString());
            }

            rs = ps.executeQuery();
            while (rs.next()) {

                final HashMap<String, Object> result = new HashMap<>();

                // Populate hashmap
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    final String columnName = rs.getMetaData().getColumnName(i + 1);
                    final Object value = rs.getObject(i + 1);

                    // Put value in hashmap if not null, otherwise just put
                    // empty string
                    result.put(columnName, (value != null ? value : ""));
                }

                results.add(new Query(result));
            }
        } catch (final SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement:", ex);
            return results;
        }

        return results;
    }

    /**
     * This function creates multiple strings in 'SQL style' to create the
     * proper tables. <br>
     * It looks at the tables that are loaded in memory and dynamically creates
     * proper SQL statements.
     *
     * @return SQL statements that will create the necessary tables when run.
     */
    public List<String> createTablesStatement() {
        // Returns a list of statements that need to be run to create the
        // tables.

        final List<String> statements = new ArrayList<String>();

        for (final Table table : this.getTables()) {
            StringBuilder statement = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table.getTableName() + " (");

            // For each column in the table, add it to the table.
            for (final Column column : table.getColumns()) {

                if (column.getDataType().equals(SQLDataType.INT)) {
                    statement.append("" + column.getColumnName() + " BIGINT");
                } else if (column.getDataType().equals(SQLDataType.TEXT)) {
                    statement.append("" + column.getColumnName() + " VARCHAR(100)");
                } else if (column.getDataType().equals(SQLDataType.DOUBLE)) {
                    statement.append("" + column.getColumnName() + " DECIMAL(20,10)");
                } else {
                    statement.append("" + column.getColumnName() + " " + column.getDataType().toString());
                }

                if (column.isPrimaryKey()) {
                    statement.append(" PRIMARY KEY");
                }

                if (column.isAutoIncrement()) {
                    statement.append(" AUTO_INCREMENT");
                }

                if (column.isNotNull()) {
                    statement.append(" NOT NULL");
                }

                if (column.isUnique()) {
                    statement.append(" UNIQUE");
                }

                statement.append(",");

            }

            if (!table.getUniqueMatched().isEmpty()) {

                statement.append("UNIQUE (");

                for (Column matched : table.getUniqueMatched()) {
                    statement.append(matched.getColumnName() + ",");
                }

                // Remove last comma
                statement = new StringBuilder(statement.substring(0, statement.lastIndexOf(",")) + ")");
            } else {
                statement = new StringBuilder(statement.substring(0, statement.lastIndexOf(",")));
            }

            statement.append(");");

            statements.add(statement.toString());

            plugin.debugMessage(ChatColor.GREEN + "Loaded table '" + table.getTableName() + "'");
        }

        return statements;
    }

    @Override
    public void loadTables() {
        // UUID table to look up uuid of players
        MySQLTable newTable = new MySQLTable(PlayerStat.PLAYERS.getTableName());

        Column id = new Column("id", true, SQLDataType.INT, true);

        id.setAutoIncrement(true);

        Column uuid = new Column("uuid", false, SQLDataType.TEXT, true, true);

        // Populate table
        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("playerName", false, SQLDataType.TEXT); // Name of
        // player
        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player join this server?
        newTable = new MySQLTable(PlayerStat.JOINS.getTableName());

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT); // How many times
        // did the player
        // join.

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player die?
        newTable = new MySQLTable(PlayerStat.DEATHS.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT); // How many times did the player die.
        newTable.addColumn("world", false, SQLDataType.TEXT); // What world did the player die.

        newTable.addUniqueMatched("uuid");
        newTable.addUniqueMatched("world");

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player catch an item and what type?
        newTable = new MySQLTable(PlayerStat.ITEMS_CAUGHT.getTableName());

        newTable.addColumn(id);

        Column caught = new Column("caught", false, SQLDataType.TEXT, true);
        Column world = new Column("world", false, SQLDataType.TEXT, true);
        uuid = new Column("uuid", false, SQLDataType.TEXT, true);

        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(caught);
        newTable.addColumn(world);

        newTable.addUniqueMatched("uuid");
        newTable.addUniqueMatched("world");
        newTable.addUniqueMatched("caught");

        this.addTable(newTable);

        // ----------------------------------------------------------
        // What block did a player place and how many times?
        newTable = new MySQLTable(PlayerStat.BLOCKS_PLACED.getTableName());

        newTable.addColumn(id);

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        Column block = new Column("block", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(block);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(block);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // What block did a player break and how many times?
        newTable = new MySQLTable(PlayerStat.BLOCKS_BROKEN.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        block = new Column("block", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(block);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(block);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // What mobs did a player kill?
        newTable = new MySQLTable(PlayerStat.KILLS_MOBS.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        Column mob = new Column("mob", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);
        Column weapon = new Column("weapon", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(mob);
        newTable.addColumn(weapon);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(mob);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched(weapon);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // What players did a player kill?
        newTable = new MySQLTable(PlayerStat.KILLS_PLAYERS.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        Column playerKilled = new Column("playerKilled", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(playerKilled);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(playerKilled);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How long did a player play (in minutes)?
        newTable = new MySQLTable(PlayerStat.TIME_PLAYED.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // What food did a player eat?
        newTable = new MySQLTable(PlayerStat.FOOD_EATEN.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        Column foodEaten = new Column("foodEaten", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(foodEaten);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(foodEaten);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How much damage has a player taken?
        newTable = new MySQLTable(PlayerStat.DAMAGE_TAKEN.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        Column cause = new Column("cause", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(cause);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(cause);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many sheep did a player shear?
        newTable = new MySQLTable(PlayerStat.TIMES_SHORN.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How far and in what way has a player travelled?
        newTable = new MySQLTable(PlayerStat.DISTANCE_TRAVELLED.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        Column moveType = new Column("moveType", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.DOUBLE);
        newTable.addColumn(world);
        newTable.addColumn(moveType);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(moveType);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How far and in what way has a player travelled?
        newTable = new MySQLTable(PlayerStat.ITEMS_CRAFTED.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        Column item = new Column("item", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(item);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(item);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How much XP did a player gain?
        newTable = new MySQLTable(PlayerStat.XP_GAINED.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player vote for this server?
        newTable = new MySQLTable(PlayerStat.VOTES.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT); // How many times
        // did the player
        // vote.

        newTable.addUniqueMatched(uuid);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many arrows did a player shoot?
        newTable = new MySQLTable(PlayerStat.ARROWS_SHOT.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player enter a bed?
        newTable = new MySQLTable(PlayerStat.ENTERED_BEDS.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player perform a command?
        newTable = new MySQLTable(PlayerStat.COMMANDS_PERFORMED.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);
        newTable.addColumn("command", false, SQLDataType.TEXT, true);
        newTable.addColumn("arguments", false, SQLDataType.TEXT, true);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched("command");
        newTable.addUniqueMatched("arguments");

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player get kicked?
        newTable = new MySQLTable(PlayerStat.TIMES_KICKED.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);
        newTable.addColumn("reason", false, SQLDataType.TEXT, true);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched("reason");

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many tools did a player break?
        newTable = new MySQLTable(PlayerStat.TOOLS_BROKEN.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);
        newTable.addColumn("item", false, SQLDataType.TEXT, true);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched("item");

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many eggs did a player throw?
        newTable = new MySQLTable(PlayerStat.EGGS_THROWN.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player switch worlds?
        newTable = new MySQLTable(PlayerStat.WORLDS_CHANGED.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);
        newTable.addColumn("destWorld", false, SQLDataType.TEXT, true);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched("destWorld");

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player fill a bucket?
        newTable = new MySQLTable(PlayerStat.BUCKETS_FILLED.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player empty a bucket?
        newTable = new MySQLTable(PlayerStat.BUCKETS_EMPTIED.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many items did a player drop?
        newTable = new MySQLTable(PlayerStat.ITEMS_DROPPED.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);
        item = new Column("item", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);
        newTable.addColumn(item);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched(item);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many items did a player pick up?
        newTable = new MySQLTable(PlayerStat.ITEMS_PICKED_UP.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);
        item = new Column("item", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);
        newTable.addColumn(item);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched(item);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player teleport?
        newTable = new MySQLTable(PlayerStat.TELEPORTS.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);
        newTable.addColumn("destWorld", false, SQLDataType.TEXT, true);
        newTable.addColumn("cause", false, SQLDataType.TEXT, true);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched("destWorld");
        newTable.addUniqueMatched("cause");

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player trade with villagers?
        newTable = new MySQLTable(PlayerStat.VILLAGER_TRADES.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);
        newTable.addColumn("trade", false, SQLDataType.TEXT, true);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched("trade");

        this.addTable(newTable);

    }

    @Override
    public void setObjects(final Table table, final Query results, final SET_OPERATION mode) {
        // Run SQLite query async to not disturb the main Server thread
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            public void run() {
                StringBuilder columnNames = new StringBuilder("(");

                StringBuilder resultNames = new StringBuilder("(");

                for (final Entry<String, Object> result : results.getEntrySet()) {
                    columnNames.append(result.getKey() + ",");

                    try {
                        // Try to check if it is an integer
                        Integer.parseInt(result.getValue().toString());
                        resultNames.append(result.getValue() + ",");
                    } catch (final NumberFormatException e) {

                        try {
                            // Try to check if it is an double
                            Double.parseDouble(result.getValue().toString());
                            resultNames.append(result.getValue().toString() + ",");
                        } catch (NumberFormatException ev) {
                            resultNames.append("'" + result.getValue().toString().replace("'", "''") + "',");
                        }
                    }

                }

                // Remove last comma
                columnNames = new StringBuilder(columnNames.substring(0, columnNames.lastIndexOf(",")) + ")");
                resultNames = new StringBuilder(resultNames.substring(0, resultNames.lastIndexOf(",")) + ")");

                String update = "INSERT INTO " + table.getTableName() + " " + columnNames.toString() + " VALUES "
                        + resultNames;

                String onDuplicate = "";

                if (results.hasColumn("value")) {
                    if (mode == SET_OPERATION.OVERRIDE) {
                        // Override current value
                        onDuplicate = " ON DUPLICATE KEY UPDATE value=" + results.getValue();
                    } else {
                        // Add to current value
                        onDuplicate = " ON DUPLICATE KEY UPDATE value=value+" + results.getValue();
                    }

                } else {
                    onDuplicate = " ON DUPLICATE KEY UPDATE playerName='" + results.getValue("playerName") + "'";
                }

                update += onDuplicate;

                try (Connection conn = getConnection()) {
                    PreparedStatement ps = null;

                    ps = conn.prepareStatement(update);
                    ps.executeUpdate();

                    return;
                } catch (final SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement:", ex);
                }
            }
        });
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHostname() {
        return hostname;
    }

    @Override
    public void setBatchObjects(final Table table, final List<Query> queries, SET_OPERATION mode) {
        // Run SQLite query async to not disturb the main Server thread

        Statement stmt = null;

        try (Connection conn = getConnection()) {
            stmt = conn.createStatement();

            for (Query query : queries) {
                StringBuilder columnNames = new StringBuilder("(");

                StringBuilder resultNames = new StringBuilder("(");

                for (final Entry<String, Object> result : query.getEntrySet()) {
                    columnNames.append(result.getKey() + ",");

                    try {
                        // Try to check if it is an integer
                        Integer.parseInt(result.getValue().toString());
                        resultNames.append(result.getValue() + ",");
                    } catch (final NumberFormatException e) {

                        try {
                            // Try to check if it is an double
                            Double.parseDouble(result.getValue().toString());
                            resultNames.append(result.getValue() + ",");
                        } catch (NumberFormatException ev) {
                            resultNames.append("'" + result.getValue().toString().replace("'", "''") + "',");
                        }
                    }

                }

                // Remove last comma
                columnNames = new StringBuilder(columnNames.substring(0, columnNames.lastIndexOf(",")) + ")");
                resultNames = new StringBuilder(resultNames.substring(0, resultNames.lastIndexOf(",")) + ")");

                String update = "INSERT INTO " + table.getTableName() + " " + columnNames.toString() + " VALUES "
                        + resultNames;

                String onDuplicate = "";

                if (query.hasColumn("value")) {
                    if (mode == SET_OPERATION.OVERRIDE) {
                        // Override current value
                        onDuplicate = " ON DUPLICATE KEY UPDATE value=" + query.getValue();
                    } else {
                        // Add to current value
                        onDuplicate = " ON DUPLICATE KEY UPDATE value=value+" + query.getValue();
                    }
                } else {
                    onDuplicate = " ON DUPLICATE KEY UPDATE playerName='" + query.getValue("playerName") + "'";
                }

                update += onDuplicate;

                stmt.addBatch(update);
            }

            stmt.executeBatch();

            if (!conn.getAutoCommit()) {
                conn.commit();
            }

        } catch (BatchUpdateException b) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement:", b);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement:", ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void purgeData(final UUID uuid) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            public void run() {

                try (Connection conn = getConnection()) {
                    for (Table table : getTables()) {
                        String update = "DELETE FROM " + table.getTableName() + " WHERE uuid='" + uuid.toString() + "'";

                        PreparedStatement ps = conn.prepareStatement(update);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public ResultSet sendQuery(final String query, final boolean wantResult) throws SQLException {

        Connection conn = null;
        PreparedStatement ps = null;

        conn = getConnection();
        ResultSet resultSet = null;

        try {
            ps = conn.prepareStatement(query);

            // If we need the result, store it.
            if (wantResult) {
                resultSet = ps.executeQuery();
            } else { // We do not need the result, so just update the database.
                ps.executeUpdate();
            }

        } catch (final SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement:", ex);

            throw ex;
        } finally {
            try {
                if (conn != null && !wantResult)
                    conn.close();
            } catch (final SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
            }
        }

        return resultSet;
    }

    @Override
    public List<ResultSet> sendQueries(final List<String> queries, boolean wantResult) throws SQLException {

        Connection conn = null;
        PreparedStatement ps = null;

        conn = getConnection();

        List<ResultSet> resultSets = null;

        for (String query : queries) {
            try {
                ps = conn.prepareStatement(query);

                if (wantResult) {
                    ResultSet resultSet = ps.executeQuery();

                    // Only add result sets that are not null.
                    if (resultSet != null) {
                        resultSets.add(resultSet);
                    }
                } else { // We do not care about result sets, so just perform an update to the database.
                    ps.executeUpdate();
                }


            } catch (final SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement:", ex);

                throw ex;
            } finally {
                try {
                    if (conn != null && !wantResult)
                        conn.close();

                } catch (final SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
                }
            }
        }

        return resultSets;
    }

    @Override
    public boolean createBackup(String identifier) {

        Connection tempConnection = null;

        String backupTableName = plugin.getConfigHandler().getBackupMySQLDatabase();
        String backupTablePrefix = identifier;

        try {
            final String url = "jdbc:mysql://" + hostname + "/";

            // Open a temporary connection to create a new database to make a back up.
            tempConnection = DriverManager.getConnection(url, username, password);

            Statement statement = tempConnection.createStatement();

            // Create a new database that acts as a backup.
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + backupTableName);

            for (Table table : this.getTables()) {

                // Drop the back up table if it already exists so we can override it.
                statement.executeUpdate("DROP TABLE IF EXISTS " + backupTableName + "." + backupTablePrefix + "_" + table.getTableName());

                // We create a table and copy the structure from the other database.
                statement.executeUpdate("CREATE TABLE " + backupTableName + "." + backupTablePrefix + "_" + table.getTableName() + " LIKE "
                        + databaseName + "." + table.getTableName());
                // Then we load all data from the original table into the new table
                statement.executeUpdate("INSERT INTO " + backupTableName + "." + backupTablePrefix + "_" + table.getTableName() + " SELECT * " +
                        "FROM " + databaseName + "." + table.getTableName());
            }

            // Close connection to prevent leakages.
            try {
                tempConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return true;
        } catch (final SQLException ex) {
            System.out.println("SQLDataStorage.connect");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            plugin.getLogger().log(Level.SEVERE, "MySQL exception on connecting: " + ex.getMessage());
            return false; // Could not make a backup.
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}