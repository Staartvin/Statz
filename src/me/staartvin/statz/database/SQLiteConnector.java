package me.staartvin.statz.database;

import java.io.File;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.ChatColor;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Column;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.Table;
import me.staartvin.statz.database.datatype.Table.SQLDataType;
import me.staartvin.statz.database.datatype.sqlite.SQLiteTable;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

public class SQLiteConnector extends DatabaseConnector {

    private final Statz plugin;
    private Connection connection;

    public SQLiteConnector(final Statz instance) {
        super(instance);
        plugin = instance;
    }

    /* (non-Javadoc)
     * @see me.staartvin.statz.database.Database#getSQLConnection()
     */
    @Override
    public Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        final File dataFile = new File(plugin.getDataFolder(), databaseName + ".db");
        if (!dataFile.exists()) {
            plugin.debugMessage(ChatColor.YELLOW + "Database not found! Creating one for you.");
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
                plugin.debugMessage(ChatColor.GREEN + "Database created!");
            } catch (final IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: " + databaseName + ".db");
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile);
            return connection;
        } catch (final Exception ex) {
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } /*catch (final ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
			}*/

        return connection;
    }

    /* (non-Javadoc)
     * @see me.staartvin.statz.database.Database#load()
     */
    @Override
    public void load() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                connection = getConnection();

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
     * proper tables.
     * <br>
     * It looks at the tables that are loaded in memory and dynamically creates
     * proper SQL statements.
     *
     * @return SQL statements that will create the necessary tables when run.
     */
    public List<String> createTablesStatement() {
        // Returns a list of statements that need to be run to create the tables.

        final List<String> statements = new ArrayList<String>();

        for (final Table table : this.getTables()) {
            StringBuilder statement = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table.getTableName() + " (");

            // For each column in the table, add it to the table.
            for (final Column column : table.getColumns()) {

                if (column.getDataType().equals(SQLDataType.INT)) {
                    statement.append("'" + column.getColumnName() + "' INTEGER");
                } else {
                    statement.append("'" + column.getColumnName() + "' " + column.getDataType().toString());
                }

                if (column.isPrimaryKey()) {
                    statement.append(" PRIMARY KEY");
                }

                if (column.isAutoIncrement()) {
                    statement.append(" AUTOINCREMENT");
                }

                if (column.isNotNull()) {
                    statement.append(" NOT NULL");
                }

                if (column.isUnique()) {
                    statement.append(" UNIQUE");
                }

                statement.append(",");

            }

			/*if (table.getPrimaryKey() == null) {
                // Remove last comma
				statement = new StringBuilder(statement.substring(0, statement.lastIndexOf(",")));
			}*/

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
        SQLiteTable newTable = new SQLiteTable("players");

        Column id = new Column("id", true, SQLDataType.INT, true);

        // Populate table
        newTable.addColumn("uuid", true, SQLDataType.TEXT); // UUID of the player
        newTable.addColumn("playerName", false, SQLDataType.TEXT); // Name of player
        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player join this server?
        newTable = new SQLiteTable(PlayerStat.JOINS.getTableName());

        newTable.addColumn("uuid", true, SQLDataType.TEXT); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT); // How many times did the player join.

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player die?
        newTable = new SQLiteTable(PlayerStat.DEATHS.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
        newTable.addColumn(id);

        newTable.addColumn("uuid", false, SQLDataType.TEXT); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT); // How many times did the player die.
        newTable.addColumn("world", false, SQLDataType.TEXT); // What world did the player die.

        newTable.addUniqueMatched("uuid");
        newTable.addUniqueMatched("world");

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many times did a player catch an item and what type?
        newTable = new SQLiteTable(PlayerStat.ITEMS_CAUGHT.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
        newTable.addColumn(id);

        Column uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        Column caught = new Column("caught", false, SQLDataType.TEXT, true);
        Column world = new Column("world", false, SQLDataType.TEXT, true);

        newTable.addColumn(uuid); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT);
        newTable.addColumn(caught);
        newTable.addColumn(world);

        newTable.addUniqueMatched(uuid);
        newTable.addUniqueMatched(caught);
        newTable.addUniqueMatched(world);

        this.addTable(newTable);

        // ----------------------------------------------------------
        // What block did a player place and how many times?
        newTable = new SQLiteTable(PlayerStat.BLOCKS_PLACED.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.BLOCKS_BROKEN.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.KILLS_MOBS.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.KILLS_PLAYERS.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.TIME_PLAYED.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.FOOD_EATEN.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.DAMAGE_TAKEN.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.TIMES_SHORN.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.DISTANCE_TRAVELLED.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
        uuid = new Column("uuid", false, SQLDataType.TEXT, true);
        typeID = new Column("moveType", false, SQLDataType.TEXT, true);
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
        // How far and in what way has a player travelled?
        newTable = new SQLiteTable(PlayerStat.ITEMS_CRAFTED.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.XP_GAINED.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.VOTES.getTableName());

        newTable.addColumn("uuid", true, SQLDataType.TEXT); // UUID of the player
        newTable.addColumn("value", false, SQLDataType.INT); // How many times did the player vote.

        this.addTable(newTable);

        // ----------------------------------------------------------
        // How many arrows did a player shoot?
        newTable = new SQLiteTable(PlayerStat.ARROWS_SHOT.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.ENTERED_BEDS.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.COMMANDS_PERFORMED.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.TIMES_KICKED.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.TOOLS_BROKEN.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.EGGS_THROWN.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.WORLDS_CHANGED.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.BUCKETS_FILLED.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        // How many times did a player fill a bucket?
        newTable = new SQLiteTable(PlayerStat.BUCKETS_EMPTIED.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.ITEMS_DROPPED.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.ITEMS_PICKED_UP.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.TELEPORTS.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        newTable = new SQLiteTable(PlayerStat.VILLAGER_TRADES.getTableName());

        id = new Column("id", true, SQLDataType.INT, true);
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
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        final List<Query> results = new ArrayList<>();

        try {
            conn = getConnection();
            if (queries != null) {
                ps = conn.prepareStatement(
                        "SELECT * FROM " + table.getTableName() + " WHERE " + StatzUtil.convertQuery(queries) + ";");
            } else {
                ps = conn.prepareStatement("SELECT * FROM " + table.getTableName());
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
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement:", ex);
            return results;
        } finally {
            try {
                if (ps != null)
                    ps.close();
                //if (conn != null)
                //conn.close();
            } catch (final SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close SQLite connection: ", ex);
            }
        }
        return results;
    }

    @Override
    public void setObjects(final Table table, final Query results, final int mode) {
        // Run SQLite query async to not disturb the main Server thread
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @SuppressWarnings("resource")
            public void run() {
                Connection conn = null;
                PreparedStatement ps = null;

                StringBuilder columnNames = new StringBuilder("(");

                StringBuilder resultNames = new StringBuilder("(");

                StringBuilder updateWhere = new StringBuilder("");

                for (final Entry<String, String> result : results.getEntrySet()) {
                    columnNames.append(result.getKey() + ",");

                    // DO NOT add for value column
                    if (!result.getKey().equalsIgnoreCase("value")) {
                        updateWhere.append(result.getKey() + "=");
                    }

                    try {
                        // Try to check if it is an integer
                        Integer.parseInt(result.getValue());
                        resultNames.append(result.getValue() + ",");

                        if (!result.getKey().equalsIgnoreCase("value")) {
                            updateWhere.append(result.getValue() + " AND ");
                        }

                    } catch (final NumberFormatException e) {
                        resultNames.append("'" + result.getValue().replace("'", "''") + "',");

                        if (!result.getKey().equalsIgnoreCase("value")) {
                            updateWhere.append("'" + result.getValue().replace("'", "''") + "' AND ");
                        }
                    }

                }

                // Remove last comma
                columnNames = new StringBuilder(columnNames.substring(0, columnNames.lastIndexOf(",")) + ")");
                resultNames = new StringBuilder(resultNames.substring(0, resultNames.lastIndexOf(",")) + ")");
                updateWhere = new StringBuilder(updateWhere.substring(0, updateWhere.lastIndexOf("AND")));

                String update;
                String updateTwo = null;

                if (mode == 1 || !results.hasKey("value")) {
                    // Override value
                    update = "INSERT OR REPLACE INTO " + table.getTableName() + " " + columnNames.toString()
                            + " VALUES " + resultNames;
                } else {
                    // Add value
                    update = "UPDATE " + table.getTableName() + " SET value=value + " + results.getValue() + " WHERE "
                            + updateWhere.toString() + ";";
                    updateTwo = "INSERT OR IGNORE INTO " + table.getTableName() + " " + columnNames.toString()
                            + " VALUES " + resultNames + ";";
                }

                //System.out.println("UPDATE Query: " + update);
                //System.out.println("Update query two: " + updateTwo);

                try {
                    conn = getConnection();
                    ps = conn.prepareStatement(update);
                    ps.executeUpdate();

                    if (updateTwo != null) {
                        ps = conn.prepareStatement(updateTwo);
                        ps.executeUpdate();
                    }

                    return;
                } catch (final SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement:", ex);
                } finally {
                    try {
                        if (ps != null)
                            ps.close();
                        //if (conn != null)
                        //conn.close();
                    } catch (final SQLException ex) {
                        plugin.getLogger().log(Level.SEVERE, "Failed to close SQLite connection: ", ex);
                    }
                }
            }
        });
    }

    @Override
    public void setBatchObjects(final Table table, final List<Query> queries, int mode) {
        // Run SQLite query async to not disturb the main Server thread

        Connection conn = getConnection();
        Statement stmt = null;

        try {
            //conn.setAutoCommit(false);
            stmt = conn.createStatement();

            for (Query query : queries) {
                StringBuilder columnNames = new StringBuilder("(");

                StringBuilder resultNames = new StringBuilder("(");

                StringBuilder updateWhere = new StringBuilder("");

                for (final Entry<String, String> result : query.getEntrySet()) {
                    columnNames.append(result.getKey() + ",");

                    // DO NOT add for value column
                    if (!result.getKey().equalsIgnoreCase("value")) {
                        updateWhere.append(result.getKey() + "=");
                    }

                    try {
                        // Try to check if it is an integer
                        Double.parseDouble(result.getValue());
                        resultNames.append(result.getValue() + ",");

                        if (!result.getKey().equalsIgnoreCase("value")) {
                            updateWhere.append(result.getValue() + " AND ");
                        }
                    } catch (final NumberFormatException e) {
                        resultNames.append("'" + result.getValue().replace("'", "''") + "',");

                        if (!result.getKey().equalsIgnoreCase("value")) {
                            updateWhere.append("'" + result.getValue().replace("'", "''") + "' AND ");
                        }
                    }

                }

                // Remove last comma
                columnNames = new StringBuilder(columnNames.substring(0, columnNames.lastIndexOf(",")) + ")");
                resultNames = new StringBuilder(resultNames.substring(0, resultNames.lastIndexOf(",")) + ")");
                updateWhere = new StringBuilder(updateWhere.substring(0, updateWhere.lastIndexOf("AND")));

                String update;
                String updateTwo = null;

                if (mode == 1 || !query.hasKey("value")) {
                    // Override value
                    update = "INSERT OR REPLACE INTO " + table.getTableName() + " " + columnNames.toString()
                            + " VALUES " + resultNames;
                } else {
                    // Add value
                    update = "UPDATE " + table.getTableName() + " SET value=value + " + query.getValue() + " WHERE "
                            + updateWhere.toString() + ";";
                    updateTwo = "INSERT OR IGNORE INTO " + table.getTableName() + " " + columnNames.toString()
                            + " VALUES " + resultNames + ";";
                }

                //System.out.println("UPDATE Query: " + update);
                //System.out.println("Update query two: " + updateTwo);

                stmt.addBatch(update);
                if (updateTwo != null) {
                    stmt.addBatch(updateTwo);
                }
            }

            @SuppressWarnings("unused")
            int[] updateCounts = stmt.executeBatch();
            //System.out.println("Updated " + updateCounts.length + " rows");

            if (!conn.getAutoCommit()) {
                conn.commit();
            }

        } catch (BatchUpdateException b) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement:", b);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement:", ex);
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
                        plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement:", ex);
                    } finally {
                        try {
                            if (ps != null)
                                ps.close();
                            //if (conn != null)
                            //conn.close();
                        } catch (final SQLException ex) {
                            plugin.getLogger().log(Level.SEVERE, "Failed to close SQLite connection: ", ex);
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
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement:", ex);
        } finally {
            try {
                if (ps != null && !wantResult)
                    ps.close();
                //if (conn != null)
                //conn.close();
            } catch (final SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close SQLite connection: ", ex);
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
                plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQLite statement:", ex);
            } finally {
                try {
                    if (ps != null && !wantResult)
                        ps.close();

                } catch (final SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to close SQLite connection: ", ex);
                }
            }
        }

        return resultSets;
    }
}