package me.staartvin.statz.importer;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.datamanager.player.PlayerStat;
import me.staartvin.statz.util.StatzUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
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

                Bukkit.getWorlds().forEach(world -> {
                    PlayerInfo info = new PlayerInfo(uuid);

                    importJoins(info, world.getName());
                    importDeaths(info, world.getName());
                    importFishCaught(info, world.getName());
                    importBlocksPlaced(info, world.getName());
                    importBlocksBroken(info, world.getName());
                    importMobsKilled(info, world.getName());
                    importPlayersKilled(info, world.getName());
                    importPlaytime(info, world.getName());
                    importFoodEaten(info, world.getName());
                    importDamageTaken(info, world.getName());
                    importDistanceTravelled(info, world.getName());
                    importItemsCrafted(info, world.getName());
                    importArrowsShot(info, world.getName());
                    importToolsBroken(info, world.getName());
                    importBedsEntered(info, world.getName());
                    importEggsThrown(info, world.getName());
                    importItemsPickedUp(info, world.getName());
                    importItemsDropped(info, world.getName());
                    importVillageTrades(info, world.getName());

                    plugin.getDataManager().setPlayerInfo(info);
                });

                playersImported++;
            }

            return playersImported;
        });
    }

    private void importJoins(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        long joins = (Long) getStatistic(object.get(), Statistic.LEAVE_GAME.getKey().toString())
                .orElse(0L);

        if (joins <= 0) return;

        playerInfo.addRow(PlayerStat.JOINS,
                StatzUtil.makeQuery(playerInfo.getUUID(), "value", joins));
    }

    private void importDeaths(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        long deaths = (Long) getStatistic(object.get(), Statistic.DEATHS.getKey().toString())
                .orElse(0L);

        if (deaths <= 0) return;

        playerInfo.addRow(PlayerStat.DEATHS, StatzUtil.makeQuery(playerInfo.getUUID(),
                "value", deaths, "world", worldName));
    }

    private void importFishCaught(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        long caught = (Long) getStatistic(object.get(), Statistic.FISH_CAUGHT.getKey().toString())
                .orElse(0L);

        if (caught <= 0) return;

        playerInfo.addRow(PlayerStat.ITEMS_CAUGHT,
                StatzUtil.makeQuery(playerInfo.getUUID(), "value", caught, "world",
                        worldName, "caught", Material.TROPICAL_FISH));
    }

    private void importBlocksPlaced(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        Optional<JSONObject> placedSection = getUsedSection(playerInfo.getUUID(), worldName);

        if (!placedSection.isPresent()) return;

        for (Material material : Material.values()) {
            if (material.isBlock()) {

                long placed = (Long) getStatistic(placedSection.get(), material.getKey().toString()).orElse(0L);

                if (placed <= 0) continue;

                playerInfo.addRow(PlayerStat.BLOCKS_PLACED,
                        StatzUtil.makeQuery(playerInfo.getUUID(), "value", placed, "world",
                                worldName, "block", material));
            }
        }
    }

    private void importBlocksBroken(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        Optional<JSONObject> minedSection = getMinedSection(playerInfo.getUUID(), worldName);

        if (!minedSection.isPresent()) return;

        for (Material material : Material.values()) {
            if (material.isBlock()) {

                long broken = (Long) getStatistic(minedSection.get(), material.getKey().toString()).orElse(0L);

                if (broken <= 0) continue;

                playerInfo.addRow(PlayerStat.BLOCKS_BROKEN,
                        StatzUtil.makeQuery(playerInfo.getUUID(), "value", broken, "world",
                                worldName, "block", material));
            }
        }
    }

    private void importMobsKilled(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        Optional<JSONObject> killedSection = getKilledSection(playerInfo.getUUID(), worldName);

        if (!killedSection.isPresent()) return;

        for (EntityType entityType : EntityType.values()) {

            if (entityType.isSpawnable()) {
                long killed = (Long) getStatistic(killedSection.get(), entityType.getKey().toString()).orElse(0L);

                if (killed <= 0) continue;

                playerInfo.addRow(PlayerStat.KILLS_MOBS,
                        StatzUtil.makeQuery(playerInfo.getUUID(), "value", killed, "world",
                                worldName, "weapon", "HAND", "mob", entityType.toString()));
            }
        }
    }

    private void importPlayersKilled(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        long playersKilled = (Long) getStatistic(object.get(), Statistic.PLAYER_KILLS.getKey().toString())
                .orElse(0L);

        if (playersKilled <= 0) return;

        playerInfo.addRow(PlayerStat.KILLS_PLAYERS,
                StatzUtil.makeQuery(playerInfo.getUUID(), "value", playersKilled, "world",
                        worldName, "playerKilled", "Notch"));
    }

    private void importPlaytime(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        long ticksPlayed = (Long) getStatistic(object.get(), Statistic.PLAY_ONE_MINUTE.getKey().toString())
                .orElse(0L);

        if (ticksPlayed <= 0) return;

        playerInfo.addRow(PlayerStat.TIME_PLAYED,
                StatzUtil.makeQuery(playerInfo.getUUID(), "value", ticksPlayed / (20 * 60), "world",
                        worldName));
    }

    private void importFoodEaten(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        Optional<JSONObject> foodSection = getUsedSection(playerInfo.getUUID(), worldName);

        if (!foodSection.isPresent()) return;

        for (Material material : Material.values()) {
            if (material.isEdible()) {

                long eaten = (Long) getStatistic(foodSection.get(), material.getKey().toString()).orElse(0L);

                if (eaten <= 0) continue;

                playerInfo.addRow(PlayerStat.FOOD_EATEN,
                        StatzUtil.makeQuery(playerInfo.getUUID(), "value", eaten, "world",
                                worldName, "foodEaten", material));
            }
        }
    }

    private void importDamageTaken(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        long damageTaken = (Long) getStatistic(object.get(), Statistic.DAMAGE_TAKEN.getKey().toString())
                .orElse(0L);

        if (damageTaken <= 0) return;

        playerInfo.addRow(PlayerStat.DAMAGE_TAKEN,
                StatzUtil.makeQuery(playerInfo.getUUID(), "value", damageTaken, "world",
                        worldName, "cause", EntityDamageEvent.DamageCause.ENTITY_ATTACK));
    }

    private void importDistanceTravelled(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        long moved = (Long) getStatistic(object.get(), Statistic.WALK_ONE_CM.getKey().toString())
                .orElse(0L) / 100L;

        if (moved > 0) {
            playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                    StatzUtil.makeQuery(playerInfo.getUUID(), "value", moved, "world",
                            worldName, "moveType", "WALK"));
        }

        moved = (Long) getStatistic(object.get(), Statistic.SWIM_ONE_CM.getKey().toString())
                .orElse(0L) / 100L;

        if (moved > 0) {
            playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                    StatzUtil.makeQuery(playerInfo.getUUID(), "value", moved, "world",
                            worldName, "moveType",
                            "SWIM"));
        }

        moved = (Long) getStatistic(object.get(), Statistic.FLY_ONE_CM.getKey().toString())
                .orElse(0L) / 100L;

        if (moved > 0) {
            playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                    StatzUtil.makeQuery(playerInfo.getUUID(), "value", moved, "world",
                            worldName, "moveType",
                            "FLY WITH ELYTRA"));
        }

        moved = (Long) getStatistic(object.get(), Statistic.BOAT_ONE_CM.getKey().toString())
                .orElse(0L) / 100L;

        if (moved > 0) {
            playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                    StatzUtil.makeQuery(playerInfo.getUUID(), "value", moved, "world",
                            worldName, "moveType",
                            "BOAT"));
        }

        moved = (Long) getStatistic(object.get(), Statistic.MINECART_ONE_CM.getKey().toString())
                .orElse(0L) / 100L;

        if (moved > 0) {
            playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                    StatzUtil.makeQuery(playerInfo.getUUID(), "value", moved, "world",
                            worldName, "moveType",
                            "MINECART"));
        }

        moved = (Long) getStatistic(object.get(), Statistic.HORSE_ONE_CM.getKey().toString())
                .orElse(0L) / 100L;

        if (moved > 0) {
            playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                    StatzUtil.makeQuery(playerInfo.getUUID(), "value", moved, "world",
                            worldName, "moveType",
                            "HORSE"));
        }

        moved = (Long) getStatistic(object.get(), Statistic.PIG_ONE_CM.getKey().toString())
                .orElse(0L) / 100L;

        if (moved > 0) {
            playerInfo.addRow(PlayerStat.DISTANCE_TRAVELLED,
                    StatzUtil.makeQuery(playerInfo.getUUID(), "value", moved, "world",
                            worldName, "moveType",
                            "PIG"));
        }
    }

    private void importItemsCrafted(PlayerInfo playerInfo, String worldName) {

        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        Optional<JSONObject> craftedSection = getCraftedSection(playerInfo.getUUID(), worldName);

        if (!craftedSection.isPresent()) return;

        for (Material material : Material.values()) {
            if (material.isItem()) {

                long itemsCrafted = (Long) getStatistic(craftedSection.get(), material.getKey().toString()).orElse(0L);

                if (itemsCrafted <= 0) continue;

                playerInfo.addRow(PlayerStat.ITEMS_CRAFTED,
                        StatzUtil.makeQuery(playerInfo.getUUID(), "value", itemsCrafted, "world",
                                worldName, "item", material));
            }
        }
    }

    private void importArrowsShot(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        Optional<JSONObject> usedSection = getUsedSection(playerInfo.getUUID(), worldName);

        if (!usedSection.isPresent()) return;

        long bowUsed = (Long) getStatistic(usedSection.get(), Material.BOW.getKey().toString()).orElse(0L);

        if (bowUsed <= 0) return;

        playerInfo.addRow(PlayerStat.ARROWS_SHOT,
                StatzUtil.makeQuery(playerInfo.getUUID(), "value", bowUsed, "world",
                        worldName));
    }

    private void importBedsEntered(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        long bedsEntered =
                (Long) getStatistic(object.get(), Statistic.SLEEP_IN_BED.getKey().toString()).orElse(0L);

        if (bedsEntered <= 0) return;

        playerInfo.addRow(PlayerStat.ENTERED_BEDS,
                StatzUtil.makeQuery(playerInfo.getUUID(), "value", bedsEntered, "world",
                        worldName));
    }

    private void importToolsBroken(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        Optional<JSONObject> brokenSection = getBrokenSection(playerInfo.getUUID(), worldName);

        if (!brokenSection.isPresent()) return;

        for (Material material : Material.values()) {
            if (material.isItem()) {

                long broken = (Long) getStatistic(brokenSection.get(), material.getKey().toString()).orElse(0L);

                if (broken <= 0) continue;

                playerInfo.addRow(PlayerStat.TOOLS_BROKEN,
                        StatzUtil.makeQuery(playerInfo.getUUID(), "value", broken, "world",
                                worldName, "item", material));
            }
        }
    }

    private void importEggsThrown(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        Optional<JSONObject> usedSection = getUsedSection(playerInfo.getUUID(), worldName);

        if (!usedSection.isPresent()) return;

        long eggsThrown = (Long) getStatistic(usedSection.get(), Material.EGG.getKey().toString()).orElse(0L);

        if (eggsThrown <= 0) return;

        playerInfo.addRow(PlayerStat.EGGS_THROWN,
                StatzUtil.makeQuery(playerInfo.getUUID(), "value", eggsThrown, "world",
                        worldName));
    }

    private void importItemsDropped(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        Optional<JSONObject> droppedSection = getDroppedSection(playerInfo.getUUID(), worldName);

        if (!droppedSection.isPresent()) return;

        for (Material material : Material.values()) {
            long dropped = (Long) getStatistic(droppedSection.get(), material.getKey().toString()).orElse(0L);

            if (dropped <= 0) continue;

            playerInfo.addRow(PlayerStat.ITEMS_DROPPED,
                    StatzUtil.makeQuery(playerInfo.getUUID(), "value", dropped, "world",
                            worldName, "item", material));
        }
    }

    private void importItemsPickedUp(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        Optional<JSONObject> pickedUpSection = getPickedUpSection(playerInfo.getUUID(), worldName);

        if (!pickedUpSection.isPresent()) return;

        for (Material material : Material.values()) {

            long pickedUp = (Long) getStatistic(pickedUpSection.get(), material.getKey().toString()).orElse(0L);

            if (pickedUp <= 0) continue;

            playerInfo.addRow(PlayerStat.ITEMS_PICKED_UP,
                    StatzUtil.makeQuery(playerInfo.getUUID(), "value", pickedUp, "world",
                            worldName, "item", material));
        }
    }

    private void importVillageTrades(PlayerInfo playerInfo, String worldName) {
        Optional<JSONObject> object = getCustomSection(playerInfo.getUUID(), worldName);

        if (!object.isPresent()) {
            return;
        }

        long traded =
                (Long) getStatistic(object.get(), Statistic.TRADED_WITH_VILLAGER.getKey().toString()).orElse(0L);

        if (traded <= 0) return;

        playerInfo.addRow(PlayerStat.VILLAGER_TRADES,
                StatzUtil.makeQuery(playerInfo.getUUID(), "value", traded, "world",
                        worldName, "trade", Material.STICK));
    }

    private Optional<JSONObject> getUserStatisticsFile(UUID uuid, String worldName)
            throws IOException, ParseException {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(worldName);

        World world = Bukkit.getWorld(worldName);

        if (world == null) return Optional.empty();

        File worldFolder = new File(world.getWorldFolder(), "stats");
        File playerStatistics = new File(worldFolder, uuid.toString() + ".json");

        if (!playerStatistics.exists()) {
            return Optional.empty();
        }

        JSONObject rootObject = (JSONObject) new JSONParser().parse(new FileReader(playerStatistics));

        if (rootObject == null) return Optional.empty();

        if (rootObject.containsKey("stats")) {
            return Optional.ofNullable((JSONObject) rootObject.get("stats"));
        }

        return Optional.empty();
    }

    private Optional<JSONObject> getCustomSection(UUID uuid, String worldName) {
        try {
            return getStatisticSubsection(getUserStatisticsFile(uuid, worldName).orElse(null), "minecraft:custom");
        } catch (IOException | ParseException e) {
            return Optional.empty();
        }
    }

    private Optional<JSONObject> getMinedSection(UUID uuid, String worldName) {
        try {
            return getStatisticSubsection(getUserStatisticsFile(uuid, worldName).orElse(null), "minecraft:mined");
        } catch (IOException | ParseException e) {
            return Optional.empty();
        }
    }

    private Optional<JSONObject> getUsedSection(UUID uuid, String worldName) {
        try {
            return getStatisticSubsection(getUserStatisticsFile(uuid, worldName).orElse(null), "minecraft:used");
        } catch (IOException | ParseException e) {
            return Optional.empty();
        }
    }

    private Optional<JSONObject> getPickedUpSection(UUID uuid, String worldName) {
        try {
            return getStatisticSubsection(getUserStatisticsFile(uuid, worldName).orElse(null), "minecraft:picked_up");
        } catch (IOException | ParseException e) {
            return Optional.empty();
        }
    }

    private Optional<JSONObject> getKilledBySection(UUID uuid, String worldName) {
        try {
            return getStatisticSubsection(getUserStatisticsFile(uuid, worldName).orElse(null), "minecraft:killed_by");
        } catch (IOException | ParseException e) {
            return Optional.empty();
        }
    }

    private Optional<JSONObject> getKilledSection(UUID uuid, String worldName) {
        try {
            return getStatisticSubsection(getUserStatisticsFile(uuid, worldName).orElse(null), "minecraft:killed");
        } catch (IOException | ParseException e) {
            return Optional.empty();
        }
    }

    private Optional<JSONObject> getCraftedSection(UUID uuid, String worldName) {
        try {
            return getStatisticSubsection(getUserStatisticsFile(uuid, worldName).orElse(null), "minecraft:crafted");
        } catch (IOException | ParseException e) {
            return Optional.empty();
        }
    }

    private Optional<JSONObject> getDroppedSection(UUID uuid, String worldName) {
        try {
            return getStatisticSubsection(getUserStatisticsFile(uuid, worldName).orElse(null), "minecraft:dropped");
        } catch (IOException | ParseException e) {
            return Optional.empty();
        }
    }

    private Optional<JSONObject> getBrokenSection(UUID uuid, String worldName) {
        try {
            return getStatisticSubsection(getUserStatisticsFile(uuid, worldName).orElse(null), "minecraft:broken");
        } catch (IOException | ParseException e) {
            return Optional.empty();
        }
    }

    private Optional<JSONObject> getStatisticSubsection(JSONObject statsSection, String path) {
        if (statsSection == null) return Optional.empty();

        return Optional.ofNullable((JSONObject) this.getStatistic(statsSection,
                path).orElse(null));
    }

    private Optional<Object> getStatistic(JSONObject root, String path) {
        if (root == null) return Optional.empty();

        if (!root.containsKey(path)) return Optional.empty();

        return Optional.ofNullable(root.get(path));
    }

}
