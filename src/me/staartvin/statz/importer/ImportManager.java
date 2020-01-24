package me.staartvin.statz.importer;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.util.StatzUtil;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This class can import data into Statz' database from other plugins. Currently supports Stats3
 * (https://dev.bukkit.org/bukkit-plugins/lolmewnstats/)
 *
 * @author Staartvin
 */
public class ImportManager {

    private Statz plugin;

    public ImportManager(Statz plugin) {
        this.plugin = plugin;
    }


    /**
     * Import statistics from the vanilla MC statistics data.
     *
     * @return number of players that have been imported.
     */
    public CompletableFuture<Integer> importFromVanilla() {
        return CompletableFuture.supplyAsync(() -> {

            List<UUID> storedPlayers = new ArrayList<>();
            try {
                storedPlayers = plugin.getDataManager().getStoredPlayers().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (storedPlayers.isEmpty()) {
                return 0;
            }

            int playersImported = 0;


            for (UUID uuid : storedPlayers) {

                PlayerInfo info = new PlayerInfo(uuid);

                Player player = plugin.getServer().getPlayer(uuid);

                if (player == null) {
                    plugin.getLogger().info("Could not import " + uuid + " as they are not online.");
                    continue;
                }

                importJoins(player, info);
                importDeaths(player, info);
                importFishCaught(player, info);
                importBlocksPlaced(player, info);
                importBlocksBroken(player, info);
                importMobsKilled(player, info);
                importPlayersKilled(player, info);
                importPlaytime(player, info);
                importFoodEaten(player, info);
                importDamageTaken(player, info);
                importDistanceTravelled(player, info);
                importItemsCrafted(player, info);
                importArrowsShot(player, info);
                importToolsBroken(player, info);
                importBedsEntered(player, info);
                importEggsThrown(player, info);
                importItemsPickedUp(player, info);
                importItemsDropped(player, info);
                importVillageTrades(player, info);

                playersImported++;

                plugin.getDataManager().setPlayerInfo(info);
            }

            return playersImported;
        });
    }

    private void importJoins(Player player, PlayerInfo playerInfo) {
        int joins = player.getStatistic(Statistic.LEAVE_GAME);

        playerInfo.addRow(PlayerStat.JOINS,
                StatzUtil.makeQuery(player.getUniqueId(), "value", joins));
    }

    private void importDeaths(Player player, PlayerInfo playerInfo) {
        int deaths = player.getStatistic(Statistic.DEATHS);

        playerInfo.addRow(PlayerStat.DEATHS,
                StatzUtil.makeQuery(player.getUniqueId(), "value", deaths, "world",
                        plugin.getServer().getWorlds().get(0).getName()));
    }

    private void importFishCaught(Player player, PlayerInfo playerInfo) {
        int caught = player.getStatistic(Statistic.FISH_CAUGHT);

        playerInfo.addRow(PlayerStat.ITEMS_CAUGHT,
                StatzUtil.makeQuery(player.getUniqueId(), "value", caught, "world",
                        plugin.getServer().getWorlds().get(0).getName(), "caught", Material.TROPICAL_FISH));
    }

    private void importBlocksPlaced(Player player, PlayerInfo playerInfo) {
        for (Material material : Material.values()) {
            if (material.isBlock()) {

                int placed = player.getStatistic(Statistic.USE_ITEM, material);

                if (placed <= 0) continue;

                playerInfo.addRow(PlayerStat.BLOCKS_PLACED,
                        StatzUtil.makeQuery(player.getUniqueId(), "value", placed, "world",
                                plugin.getServer().getWorlds().get(0).getName(), "block", material));
            }
        }
    }

    private void importBlocksBroken(Player player, PlayerInfo playerInfo) {
        for (Material material : Material.values()) {
            if (material.isBlock()) {

                int broken = player.getStatistic(Statistic.MINE_BLOCK, material);

                if (broken <= 0) continue;

                playerInfo.addRow(PlayerStat.BLOCKS_BROKEN,
                        StatzUtil.makeQuery(player.getUniqueId(), "value", broken, "world",
                                plugin.getServer().getWorlds().get(0).getName(), "block", material));
            }
        }
    }

    private void importMobsKilled(Player player, PlayerInfo playerInfo) {
        for (EntityType entityType : EntityType.values()) {

            if (entityType.isSpawnable()) {
                int killed = player.getStatistic(Statistic.KILL_ENTITY, entityType);

                if (killed <= 0) continue;

                playerInfo.addRow(PlayerStat.KILLS_MOBS,
                        StatzUtil.makeQuery(player.getUniqueId(), "value", killed, "world",
                                plugin.getServer().getWorlds().get(0).getName(), "weapon", "HAND", "mob",
                                entityType.toString()));
            }
        }
    }

    private void importPlayersKilled(Player player, PlayerInfo playerInfo) {
        int playersKilled = player.getStatistic(Statistic.PLAYER_KILLS);

        playerInfo.addRow(PlayerStat.KILLS_PLAYERS,
                StatzUtil.makeQuery(player.getUniqueId(), "value", playersKilled, "world",
                        plugin.getServer().getWorlds().get(0).getName(), "playerKilled", "Notch"));
    }

    private void importPlaytime(Player player, PlayerInfo playerInfo) {
        int ticksPlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE);

        playerInfo.addRow(PlayerStat.TIME_PLAYED,
                StatzUtil.makeQuery(player.getUniqueId(), "value", ticksPlayed / (20 * 60), "world",
                        plugin.getServer().getWorlds().get(0).getName()));
    }

    private void importFoodEaten(Player player, PlayerInfo playerInfo) {
        for (Material material : Material.values()) {
            if (material.isEdible()) {

                int eaten = player.getStatistic(Statistic.USE_ITEM, material);

                if (eaten <= 0) continue;

                playerInfo.addRow(PlayerStat.FOOD_EATEN,
                        StatzUtil.makeQuery(player.getUniqueId(), "value", eaten, "world",
                                plugin.getServer().getWorlds().get(0).getName(), "foodEaten", material));
            }
        }
    }

    private void importDamageTaken(Player player, PlayerInfo playerInfo) {
        int damageTaken = player.getStatistic(Statistic.DAMAGE_TAKEN);

        playerInfo.addRow(PlayerStat.DAMAGE_TAKEN,
                StatzUtil.makeQuery(player.getUniqueId(), "value", damageTaken, "world",
                        plugin.getServer().getWorlds().get(0).getName(), "cause",
                        EntityDamageEvent.DamageCause.ENTITY_ATTACK));
    }

    private void importDistanceTravelled(Player player, PlayerInfo playerInfo) {
        int walked = player.getStatistic(Statistic.WALK_ONE_CM) / 100;

        playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                StatzUtil.makeQuery(player.getUniqueId(), "value", walked, "world",
                        plugin.getServer().getWorlds().get(0).getName(), "moveType",
                        "WALK"));

        int swum = player.getStatistic(Statistic.SWIM_ONE_CM) / 100;

        playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                StatzUtil.makeQuery(player.getUniqueId(), "value", swum, "world",
                        plugin.getServer().getWorlds().get(0).getName(), "moveType",
                        "SWIM"));

        int fly = player.getStatistic(Statistic.FLY_ONE_CM) / 100;

        playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                StatzUtil.makeQuery(player.getUniqueId(), "value", fly, "world",
                        plugin.getServer().getWorlds().get(0).getName(), "moveType",
                        "FLY WITH ELYTRA"));

        int boated = player.getStatistic(Statistic.BOAT_ONE_CM) / 100;

        playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                StatzUtil.makeQuery(player.getUniqueId(), "value", boated, "world",
                        plugin.getServer().getWorlds().get(0).getName(), "moveType",
                        "BOAT"));

        int minecart = player.getStatistic(Statistic.MINECART_ONE_CM) / 100;

        playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                StatzUtil.makeQuery(player.getUniqueId(), "value", minecart, "world",
                        plugin.getServer().getWorlds().get(0).getName(), "moveType",
                        "MINECART"));

        int horse = player.getStatistic(Statistic.HORSE_ONE_CM) / 100;

        playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                StatzUtil.makeQuery(player.getUniqueId(), "value", horse, "world",
                        plugin.getServer().getWorlds().get(0).getName(), "moveType",
                        "HORSE"));

        int pig = player.getStatistic(Statistic.PIG_ONE_CM) / 100;

        playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                StatzUtil.makeQuery(player.getUniqueId(), "value", pig, "world",
                        plugin.getServer().getWorlds().get(0).getName(), "moveType",
                        "PIG"));
    }

    private void importItemsCrafted(Player player, PlayerInfo playerInfo) {
        for (Material material : Material.values()) {
            if (material.isItem()) {

                int itemsCrafted = player.getStatistic(Statistic.CRAFT_ITEM, material);

                if (itemsCrafted <= 0) continue;

                playerInfo.addRow(PlayerStat.ITEMS_CRAFTED,
                        StatzUtil.makeQuery(player.getUniqueId(), "value", itemsCrafted, "world",
                                plugin.getServer().getWorlds().get(0).getName(), "item", material));
            }
        }
    }

    private void importArrowsShot(Player player, PlayerInfo playerInfo) {
        int bowUsed = player.getStatistic(Statistic.USE_ITEM, Material.BOW);

        playerInfo.addRow(PlayerStat.ARROWS_SHOT,
                StatzUtil.makeQuery(player.getUniqueId(), "value", bowUsed, "world",
                        plugin.getServer().getWorlds().get(0).getName()));
    }

    private void importBedsEntered(Player player, PlayerInfo playerInfo) {
        int bedsEntered = player.getStatistic(Statistic.SLEEP_IN_BED);

        playerInfo.addRow(PlayerStat.ENTERED_BEDS,
                StatzUtil.makeQuery(player.getUniqueId(), "value", bedsEntered, "world",
                        plugin.getServer().getWorlds().get(0).getName()));
    }

    private void importToolsBroken(Player player, PlayerInfo playerInfo) {
        for (Material material : Material.values()) {
            if (material.isItem()) {

                int broken = player.getStatistic(Statistic.BREAK_ITEM, material);

                if (broken <= 0) continue;

                playerInfo.addRow(PlayerStat.TOOLS_BROKEN,
                        StatzUtil.makeQuery(player.getUniqueId(), "value", broken, "world",
                                plugin.getServer().getWorlds().get(0).getName(), "item", material));
            }
        }
    }

    private void importEggsThrown(Player player, PlayerInfo playerInfo) {
        int eggsThrown = player.getStatistic(Statistic.USE_ITEM, Material.EGG);

        playerInfo.addRow(PlayerStat.EGGS_THROWN,
                StatzUtil.makeQuery(player.getUniqueId(), "value", eggsThrown, "world",
                        plugin.getServer().getWorlds().get(0).getName()));
    }

    private void importItemsDropped(Player player, PlayerInfo playerInfo) {
        for (Material material : Material.values()) {
            int dropped = player.getStatistic(Statistic.DROP, material);

            if (dropped <= 0) continue;

            playerInfo.addRow(PlayerStat.ITEMS_DROPPED,
                    StatzUtil.makeQuery(player.getUniqueId(), "value", dropped, "world",
                            plugin.getServer().getWorlds().get(0).getName(), "item", material));
        }
    }

    private void importItemsPickedUp(Player player, PlayerInfo playerInfo) {
        for (Material material : Material.values()) {
            int pickedUp = player.getStatistic(Statistic.PICKUP, material);

            if (pickedUp <= 0) continue;

            playerInfo.addRow(PlayerStat.ITEMS_PICKED_UP,
                    StatzUtil.makeQuery(player.getUniqueId(), "value", pickedUp, "world",
                            plugin.getServer().getWorlds().get(0).getName(), "item", material));
        }
    }

    private void importVillageTrades(Player player, PlayerInfo playerInfo) {
        int traded = player.getStatistic(Statistic.TRADED_WITH_VILLAGER);

        playerInfo.addRow(PlayerStat.VILLAGER_TRADES,
                StatzUtil.makeQuery(player.getUniqueId(), "value", traded, "world",
                        plugin.getServer().getWorlds().get(0).getName(), "trade", Material.STICK));
    }

}
