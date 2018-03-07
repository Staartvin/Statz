package me.staartvin.statz.database;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Column;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.Table;
import me.staartvin.statz.database.datatype.Table.SQLDataType;
import me.staartvin.statz.database.datatype.mysql.MySQLTable;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;
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
    private Connection connection;

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

    private String hostname = "localhost:3306";
    private String password = "";
    private String username = "root";

    /*
     * (non-Javadoc)
     * 
     * @see me.staartvin.statz.database.Database#getSQLConnection()
     */
    @Override
    public synchronized Connection getConnection() {

        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            final String url = "jdbc:mysql://" + hostname + "/" + DatabaseConnector.databaseName
                    + "?rewriteBatchedStatements=true&autoReconnect=true&useSSL=false";

            connection = DriverManager.getConnection(url, username, password);
        } catch (final SQLException ex) {
            System.out.println("SQLDataStorage.connect");
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            plugin.getLogger().log(Level.SEVERE, "MySQL exception on initialize: " + ex.getMessage());
            return null;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return connection;

    }

    /*
     * (non-Javadoc)
     * 
     * @see me.staartvin.statz.database.Database#load()
     */
    @Override
    public void load() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                connection = getConnection();

                // Did not properly connect to database
                if (connection == null) {
                    plugin.debugMessage(
                            ChatColor.RED + "I could not connect to your database! Are your credentials correct?");
                    return;
                }

                try {
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
            }
        });
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

            plugin.debugMessage(ChatColor.BLUE + "Loaded table '" + table.getTableName() + "'");
        }

        return statements;
    }

    @Override
    public void loadTables() {
        // UUID table to look up uuid of players
        MySQLTable newTable = new MySQLTable("players");

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
        Column typeID = new Column("typeid", false, SQLDataType.INT, true);
        Column dataValue = new Column("datavalue", false, SQLDataType.INT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(typeID);
        newTable.addColumn(dataValue);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(typeID);
        newTable.addUniqueMatched(dataValue);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // What block did a player break and how many times?
        newTable = new MySQLTable(PlayerStat.BLOCKS_BROKEN.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        typeID = new Column("typeid", false, SQLDataType.INT, true);
        dataValue = new Column("datavalue", false, SQLDataType.INT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(typeID);
        newTable.addColumn(dataValue);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(typeID);
        newTable.addUniqueMatched(dataValue);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // What mobs did a player kill?
        newTable = new MySQLTable(PlayerStat.KILLS_MOBS.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        typeID = new Column("mob", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);
        Column weapon = new Column("weapon", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(typeID);
        newTable.addColumn(weapon);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(typeID);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched(weapon);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // What players did a player kill?
        newTable = new MySQLTable(PlayerStat.KILLS_PLAYERS.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        typeID = new Column("playerKilled", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(typeID);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(typeID);
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
        typeID = new Column("foodEaten", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(typeID);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(typeID);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How much damage has a player taken?
        newTable = new MySQLTable(PlayerStat.DAMAGE_TAKEN.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        typeID = new Column("cause", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(typeID);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(typeID);
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
        typeID = new Column("moveType", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.DOUBLE);
        newTable.addColumn(world);
        newTable.addColumn(typeID);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(typeID);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How far and in what way has a player travelled?
        newTable = new MySQLTable(PlayerStat.ITEMS_CRAFTED.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        typeID = new Column("item", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(world);
        newTable.addColumn(typeID);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(typeID);
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
        typeID = new Column("forceShot", false, SQLDataType.DOUBLE, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);
        newTable.addColumn(typeID);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched(typeID);

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
        typeID = new Column("item", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);
        newTable.addColumn(typeID);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched(typeID);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many items did a player pick up?
        newTable = new MySQLTable(PlayerStat.ITEMS_PICKED_UP.getTableName());

        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        world = new Column("world", false, SQLDataType.TEXT, true);
        typeID = new Column("item", false, SQLDataType.TEXT, true);

        newTable.addColumn(id);
        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT, true);
        newTable.addColumn(world);
        newTable.addColumn(typeID);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(world);
        newTable.addUniqueMatched(typeID);

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
    public List<Query> getObjects(Table table, Query queries) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        final List<Query> results = new ArrayList<>();

        if (table == null) {
            plugin.debugMessage("Tried to get data from a null table! This means some tables are not setup");
            return results;
        }

        try {
            connection = getConnection();
            if (queries != null) {
                ps = connection.prepareStatement(
                        "SELECT * FROM " + table.getTableName() + " WHERE " + StatzUtil.convertQuery(queries) + ";");
            } else {
                ps = connection.prepareStatement("SELECT * FROM " + table.getTableName());
            }

            rs = ps.executeQuery();
            while (rs.next()) {

                final HashMap<String, String> result = new HashMap<>();

                // Populate hashmap
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    final String columnName = rs.getMetaData().getColumnName(i + 1);
                    final String value = rs.getObject(i + 1).toString();

                    // Put value in hashmap if not null, otherwise just put
                    // empty string
                    result.put(columnName, (value != null ? value : ""));
                }

                results.add(new Query(result));
            }
        } catch (final SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement:", ex);
            return results;
        } finally {
            try {
                if (ps != null)
                    ps.close();
                // if (conn != null)
                // conn.close();
            } catch (final SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
            }
        }
        return results;
    }

    @Override
    public void setObjects(final Table table, final Query results, final int mode) {
        // Run SQLite query async to not disturb the main Server thread
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            public void run() {
                Connection conn = null;
                PreparedStatement ps = null;

                StringBuilder columnNames = new StringBuilder("(");

                StringBuilder resultNames = new StringBuilder("(");

                for (final Entry<String, String> result : results.getEntrySet()) {
                    columnNames.append(result.getKey() + ",");

                    try {
                        // Try to check if it is an integer
                        Integer.parseInt(result.getValue());
                        resultNames.append(result.getValue() + ",");
                    } catch (final NumberFormatException e) {

                        try {
                            // Try to check if it is an double
                            Double.parseDouble(result.getValue());
                            resultNames.append(result.getValue() + ",");
                        } catch (NumberFormatException ev) {
                            resultNames.append("'" + result.getValue().replace("'", "''") + "',");
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
                    if (mode == 1) {
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

                try {
                    conn = getConnection();
                    ps = conn.prepareStatement(update);
                    ps.executeUpdate();

                    return;
                } catch (final SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement:", ex);
                } finally {
                    try {
                        if (ps != null)
                            ps.close();
                    } catch (final SQLException ex) {
                        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
                    }
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
    public void setBatchObjects(final Table table, final List<Query> queries, int mode) {
        // Run SQLite query async to not disturb the main Server thread

        Connection conn = getConnection();
        Statement stmt = null;

        try {
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            for (Query query : queries) {
                StringBuilder columnNames = new StringBuilder("(");

                StringBuilder resultNames = new StringBuilder("(");

                for (final Entry<String, String> result : query.getEntrySet()) {
                    columnNames.append(result.getKey() + ",");

                    try {
                        // Try to check if it is an integer
                        Integer.parseInt(result.getValue());
                        resultNames.append(result.getValue() + ",");
                    } catch (final NumberFormatException e) {

                        try {
                            // Try to check if it is an double
                            Double.parseDouble(result.getValue());
                            resultNames.append(result.getValue() + ",");
                        } catch (NumberFormatException ev) {
                            resultNames.append("'" + result.getValue().replace("'", "''") + "',");
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
                    if (mode == 1) {
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void purgeData(final UUID uuid) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            public void run() {

                Connection conn = null;
                PreparedStatement ps = null;

                conn = getConnection();

                for (Table table : getTables()) {
                    String update = "DELETE FROM " + table.getTableName() + " WHERE uuid='" + uuid.toString() + "'";

                    try {
                        ps = conn.prepareStatement(update);
                        ps.executeUpdate();

                    } catch (final SQLException ex) {
                        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement:", ex);
                    } finally {
                        try {
                            if (ps != null)
                                ps.close();
                            // if (conn != null)
                            // conn.close();
                        } catch (final SQLException ex) {
                            plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
                        }
                    }
                }

            }
        });
    }

    @Override
    public ResultSet sendQuery(final String query, final boolean wantResult) {

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
        } finally {
            try {
                if (ps != null && !wantResult)
                    ps.close();
                //if (conn != null)
                //conn.close();
            } catch (final SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
            }
        }

        return resultSet;
    }

    @Override
    public List<ResultSet> sendQueries(final List<String> queries, boolean wantResult) {

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
            } finally {
                try {
                    if (ps != null && !wantResult)
                        ps.close();

                } catch (final SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
                }
            }
        }

        return resultSets;
    }
}