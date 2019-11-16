package me.staartvin.statz.patches;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.DatabaseConnector;
import me.staartvin.statz.datamanager.player.PlayerStat;
import org.bukkit.Material;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This patch updates the database schemas so all old names of edible items are updated to the new names of Minecraft
 * 1.13
 */
public class RenameFoodNamesPatch extends Patch {

    public RenameFoodNamesPatch(Statz plugin) {
        super(plugin);
    }

    @Override
    public boolean applyMySQLChanges() {

        List<String> queries = new ArrayList<>();

        // Update names of old materials to their new equivalents
        // For FOOD EATEN
        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.MUSHROOM_STEW.name() + "' WHERE foodEaten='MUSHROOM_SOUP'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.PORKCHOP.name() + "' WHERE foodEaten='PORK'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.COOKED_PORKCHOP.name() + "' WHERE foodEaten='GRILLED_PORK'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.COD.name() + "' WHERE foodEaten='RAW_FISH'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.COOKED_COD.name() + "' WHERE foodEaten='COOKED_FISH'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.SALMON.name() + "' WHERE foodEaten='RAW_SALMON'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.COOKED_SALMON.name() + "' WHERE foodEaten='COOKED_SALMON'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.TROPICAL_FISH.name() + "' WHERE foodEaten='CLOWNFISH'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.BEEF.name() + "' WHERE foodEaten='RAW_BEEF'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.COOKED_BEEF.name() + "' WHERE foodEaten='COOKED_BEEF'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.CHICKEN.name() + "' WHERE foodEaten='RAW_CHICKEN'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.COOKED_CHICKEN.name() + "' WHERE foodEaten='COOKED_CHICKEN'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.POTATO.name() + "' WHERE foodEaten='POTATO_ITEM'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.CARROT.name() + "' WHERE foodEaten='CARROT_ITEM'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.MELON_SLICE.name() + "' WHERE foodEaten='MELON'");

        // For Items caught

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.MUSHROOM_STEW.name() + "' WHERE caught='MUSHROOM_SOUP'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.PORKCHOP.name() + "' WHERE caught='PORK'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.COOKED_PORKCHOP.name() + "' WHERE caught='GRILLED_PORK'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.COD.name() + "' WHERE caught='RAW_FISH'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.COOKED_COD.name() + "' WHERE caught='COOKED_FISH'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.SALMON.name() + "' WHERE caught='RAW_SALMON'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.COOKED_SALMON.name() + "' WHERE caught='COOKED_SALMON'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.TROPICAL_FISH.name() + "' WHERE caught='CLOWNFISH'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.BEEF.name() + "' WHERE caught='RAW_BEEF'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.COOKED_BEEF.name() + "' WHERE caught='COOKED_BEEF'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.CHICKEN.name() + "' WHERE caught='RAW_CHICKEN'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.COOKED_CHICKEN.name() + "' WHERE caught='COOKED_CHICKEN'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.POTATO.name() + "' WHERE caught='POTATO_ITEM'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.CARROT.name() + "' WHERE caught='CARROT_ITEM'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.MELON_SLICE.name() + "' WHERE caught='MELON'");

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
        return "Alter food names for 1.13";
    }

    @Override
    public int getPatchId() {
        return 5;
    }

    @Override
    public boolean isPatchNeeded() {
        String foodTable = this.getDatabaseConnector().getTable(PlayerStat.FOOD_EATEN).getTableName();
        String itemsCaughtTable = this.getDatabaseConnector().getTable(PlayerStat.ITEMS_CAUGHT).getTableName();

        try (ResultSet result = this.getDatabaseConnector().sendQuery("SELECT uuid FROM " + foodTable + " WHERE " +
                "foodEaten='RAW_CHICKEN' OR foodEaten='RAW_BEEF' OR foodEaten='GRILLED_PORK';", true)) {

            // No result returned, so something must've gone wrong.
            if (result == null) {
                return true;
            }

            // If there are rows found, it must mean we need to patch.
            int rows = 0;

            while (result.next()) {
                rows++;
            }

            // If there are rows found, it must mean we need to patch.
            if (rows != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }

        try (ResultSet result = this.getDatabaseConnector().sendQuery("SELECT uuid FROM " + itemsCaughtTable + " " +
                "WHERE " +
                "caught='RAW_CHICKEN' OR caught='RAW_BEEF' OR caught='GRILLED_PORK';", true)) {

            // No result returned, so something must've gone wrong.
            if (result == null) {
                return true;
            }

            // If there are rows found, it must mean we need to patch.
            int rows = 0;

            while (result.next()) {
                rows++;
            }

            // If there are rows found, it must mean we need to patch.
            if (rows != 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }

        // If both queries returned no result, it means we need no patching.
        return false;
    }

    @Override
    public boolean applySQLiteChanges() {

        List<String> queries = new ArrayList<>();

        // Update names of old materials to their new equivalents
        // For FOOD EATEN
        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.MUSHROOM_STEW.name() + "' WHERE foodEaten='MUSHROOM_SOUP'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.PORKCHOP.name() + "' WHERE foodEaten='PORK'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.COOKED_PORKCHOP.name() + "' WHERE foodEaten='GRILLED_PORK'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.COD.name() + "' WHERE foodEaten='RAW_FISH'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.COOKED_COD.name() + "' WHERE foodEaten='COOKED_FISH'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.SALMON.name() + "' WHERE foodEaten='RAW_SALMON'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.COOKED_SALMON.name() + "' WHERE foodEaten='COOKED_SALMON'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.TROPICAL_FISH.name() + "' WHERE foodEaten='CLOWNFISH'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.BEEF.name() + "' WHERE foodEaten='RAW_BEEF'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.COOKED_BEEF.name() + "' WHERE foodEaten='COOKED_BEEF'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.CHICKEN.name() + "' WHERE foodEaten='RAW_CHICKEN'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.COOKED_CHICKEN.name() + "' WHERE foodEaten='COOKED_CHICKEN'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.POTATO.name() + "' WHERE foodEaten='POTATO_ITEM'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.CARROT.name() + "' WHERE foodEaten='CARROT_ITEM'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.FOOD_EATEN).getTableName() + " SET foodEaten='"
                + Material.MELON_SLICE.name() + "' WHERE foodEaten='MELON'");

        // For Items caught

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.MUSHROOM_STEW.name() + "' WHERE caught='MUSHROOM_SOUP'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.PORKCHOP.name() + "' WHERE caught='PORK'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.COOKED_PORKCHOP.name() + "' WHERE caught='GRILLED_PORK'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.COD.name() + "' WHERE caught='RAW_FISH'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.COOKED_COD.name() + "' WHERE caught='COOKED_FISH'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.SALMON.name() + "' WHERE caught='RAW_SALMON'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.COOKED_SALMON.name() + "' WHERE caught='COOKED_SALMON'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.TROPICAL_FISH.name() + "' WHERE caught='CLOWNFISH'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.BEEF.name() + "' WHERE caught='RAW_BEEF'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.COOKED_BEEF.name() + "' WHERE caught='COOKED_BEEF'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.CHICKEN.name() + "' WHERE caught='RAW_CHICKEN'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.COOKED_CHICKEN.name() + "' WHERE caught='COOKED_CHICKEN'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.POTATO.name() + "' WHERE caught='POTATO_ITEM'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.CARROT.name() + "' WHERE caught='CARROT_ITEM'");

        queries.add("UPDATE " + DatabaseConnector.getTable(PlayerStat.ITEMS_CAUGHT).getTableName() + " SET caught='"
                + Material.MELON_SLICE.name() + "' WHERE caught='MELON'");

        try {
            this.getDatabaseConnector().sendQueries(queries, false);

            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.getStatz().getLogger().warning("Failed to patch SQLite database for patch " + this.getPatchId());
            return false;
        }

    }

}
