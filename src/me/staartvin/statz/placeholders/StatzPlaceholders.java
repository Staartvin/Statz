package me.staartvin.statz.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.datamanager.player.PlayerStat;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class StatzPlaceholders extends PlaceholderExpansion {

    Statz plugin;

    public StatzPlaceholders(Statz instance) {
        this.plugin = instance;
    }

    @Override
    public @NotNull
    String getIdentifier() {
        return "statz";
    }

    @Override
    public @NotNull
    String getAuthor() {
        return "Staartvin";
    }

    @Override
    public @NotNull
    String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        // If data of player is not loaded, first load it.
        if (!plugin.getDataManager().isPlayerLoaded(player.getUniqueId())) {
            plugin.getDataManager().loadPlayerData(player.getUniqueId());
        }


        // Regular placeholders
        if (params.equalsIgnoreCase("joins")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.JOINS, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("deaths")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.DEATHS, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("blocks_broken")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.BLOCKS_BROKEN, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("blocks_placed")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.BLOCKS_PLACED, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("damage_taken")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.DAMAGE_TAKEN, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("distance_travelled")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.DISTANCE_TRAVELLED, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("food_eaten")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.FOOD_EATEN, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("crafted_items")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.ITEMS_CRAFTED, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("caught_items")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.ITEMS_CAUGHT, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("players_killed")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.KILLS_PLAYERS, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("time_played")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.TIME_PLAYED, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("times_shorn")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.TIMES_SHORN, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("xp_gained")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.XP_GAINED, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("mobs_killed")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.KILLS_MOBS, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("villager_trades")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.VILLAGER_TRADES, player.getUniqueId()) + "";
        } else if (params.equalsIgnoreCase("votes")) {
            return plugin.getStatzAPI().getSpecificData(PlayerStat.VOTES, player.getUniqueId()) + "";
        }

        // More specific placeholders
        else if (params.toLowerCase(Locale.ROOT).startsWith("mobs_killed_")) {
            EntityType type;
            try {
                type = EntityType.valueOf(params.replace("mobs_killed_", "").toUpperCase(Locale.ROOT));
                return plugin.getStatzAPI().getSpecificData(PlayerStat.KILLS_MOBS, player.getUniqueId(),
                        new RowRequirement("mob", type.toString())) + "";
            } catch (Exception e) {
                return "Unknown mob";
            }
        } else if (params.toLowerCase(Locale.ROOT).startsWith("blocks_broken_")) {
            Material type;
            try {
                type = Material.valueOf(params.replace("blocks_broken_", "").toUpperCase(Locale.ROOT));
                return plugin.getStatzAPI().getSpecificData(PlayerStat.BLOCKS_BROKEN, player.getUniqueId(),
                        new RowRequirement("block", type.toString())) + "";
            } catch (Exception e) {
                return "Unknown block";
            }
        } else if (params.toLowerCase(Locale.ROOT).startsWith("blocks_placed_")) {
            Material type;
            try {
                type = Material.valueOf(params.replace("blocks_placed_", "").toUpperCase(Locale.ROOT));
                return plugin.getStatzAPI().getSpecificData(PlayerStat.BLOCKS_PLACED, player.getUniqueId(),
                        new RowRequirement("block", type.toString())) + "";
            } catch (Exception e) {
                return "Unknown block";
            }
        } else if (params.toLowerCase(Locale.ROOT).startsWith("food_eaten_")) {
            Material type;
            try {
                type = Material.valueOf(params.replace("food_eaten_", "").toUpperCase(Locale.ROOT));
                return plugin.getStatzAPI().getSpecificData(PlayerStat.FOOD_EATEN, player.getUniqueId(),
                        new RowRequirement("foodEaten", type.toString())) + "";
            } catch (Exception e) {
                return "Unknown food";
            }
        } else if (params.toLowerCase(Locale.ROOT).startsWith("villager_trades_")) {
            Material type;
            try {
                type = Material.valueOf(params.replace("villager_trades_", "").toUpperCase(Locale.ROOT));
                return plugin.getStatzAPI().getSpecificData(PlayerStat.VILLAGER_TRADES, player.getUniqueId(),
                        new RowRequirement("item", type.toString())) + "";
            } catch (Exception e) {
                return "Unknown material";
            }
        } else if (params.toLowerCase(Locale.ROOT).startsWith("distance_travelled_")) {
            try {
                return "" + (int) Math.round(plugin.getStatzAPI().getSpecificData(PlayerStat.DISTANCE_TRAVELLED,
                        player.getUniqueId(),
                        new RowRequirement("moveType",
                                params.replace("distance_travelled_", "").toUpperCase(Locale.ROOT))));
            } catch (Exception e) {
                return "Unknown movementType";
            }
        } else if (params.toLowerCase(Locale.ROOT).startsWith("deaths_")) {
            try {
                return plugin.getStatzAPI().getSpecificData(PlayerStat.DEATHS, player.getUniqueId(),
                        new RowRequirement("world", params.replace("deaths_", ""))) + "";
            } catch (Exception e) {
                return "Unknown world";
            }
        }


        // Return null as default because it does not seem to be a valid placeholder.
        return null;
    }
}
