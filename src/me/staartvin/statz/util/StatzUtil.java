package me.staartvin.statz.util;

import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.player.PlayerStat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.entity.Rabbit.Type;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Material.*;

public class StatzUtil {

    // This is true if player is gliding with an elytra
    public static HashMap<UUID, Boolean> isGliding = new HashMap<>();

    public static Map<Material, List<Material>> materialsMap = new HashMap<>();

    static {
        materialsMap.put(STONE, Arrays.asList(GRANITE, POLISHED_GRANITE, DIORITE, POLISHED_DIORITE,
                ANDESITE, POLISHED_ANDESITE));
        materialsMap.put(DIRT, Arrays.asList(COARSE_DIRT, PODZOL));
        materialsMap.put(OAK_PLANKS, Arrays.asList(SPRUCE_PLANKS, BIRCH_PLANKS, JUNGLE_PLANKS, ACACIA_PLANKS,
                DARK_OAK_PLANKS));
        materialsMap.put(OAK_SAPLING, Arrays.asList(SPRUCE_SAPLING, BIRCH_SAPLING, JUNGLE_SAPLING, ACACIA_SAPLING,
                DARK_OAK_SAPLING));
        materialsMap.put(SAND, Arrays.asList(RED_SAND));
        materialsMap.put(OAK_LOG, Arrays.asList(SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, OAK_WOOD,
                SPRUCE_WOOD, BIRCH_WOOD, JUNGLE_WOOD));
        materialsMap.put(ACACIA_LOG, Arrays.asList(DARK_OAK_LOG, ACACIA_WOOD, DARK_OAK_WOOD));
        materialsMap.put(OAK_LEAVES, Arrays.asList(SPRUCE_LEAVES, BIRCH_LEAVES, JUNGLE_LEAVES));
        materialsMap.put(ACACIA_LEAVES, Arrays.asList(DARK_OAK_LEAVES));
        materialsMap.put(SPONGE, Arrays.asList(WET_SPONGE));
        materialsMap.put(SANDSTONE, Arrays.asList(CHISELED_SANDSTONE, CUT_SANDSTONE));
        materialsMap.put(DEAD_BUSH, Arrays.asList(GRASS, FERN));
        materialsMap.put(WHITE_WOOL, Arrays.asList(ORANGE_WOOL, MAGENTA_WOOL, LIGHT_BLUE_WOOL, YELLOW_WOOL,
                LIME_WOOL, PINK_WOOL, GRAY_WOOL, LIGHT_GRAY_WOOL, CYAN_WOOL, PURPLE_WOOL, BLUE_WOOL, BROWN_WOOL,
                GREEN_WOOL, RED_WOOL, BLACK_WOOL));
        materialsMap.put(POPPY, Arrays.asList(BLUE_ORCHID, ALLIUM, AZURE_BLUET, RED_TULIP, ORANGE_TULIP, WHITE_TULIP,
                PINK_TULIP, OXEYE_DAISY));


    }

    /**
     * Checks whether the current version is higher than the given version
     *
     * @param versionCheck Version to check
     * @return true if the current version on this server is higher than the given version
     */
    public static boolean isHigherVersion(String versionCheck) {
        String currentVersion = getMinecraftVersion();

        if (currentVersion.equalsIgnoreCase("Unknown") || !currentVersion.contains(".") || versionCheck == null
                || !versionCheck.contains("."))
            return false;

        List<String> splitCur = new ArrayList<>();
        Collections.addAll(splitCur, currentVersion.split("\\."));

        List<String> splitCheck = new ArrayList<>();
        Collections.addAll(splitCheck, versionCheck.split("\\."));

        if (splitCur.size() < 1 || splitCheck.size() < 1) {
            return false;
        }

        System.out.println(Integer.parseInt(splitCheck.get(0).replaceAll("[^\\d.]", "")));
        System.out.println(Integer.parseInt(splitCur.get(0)
                .replaceAll("[^\\d.]", "")));

        // Check if the first digit of the version to be checked is higher than the first digit of the current
        // version of MC
        if (Integer.parseInt(splitCheck.get(0).replaceAll("[^\\d.]", "")) > Integer.parseInt(splitCur.get(0)
                .replaceAll("[^\\d.]", ""))) {
            return false;
        }

        if (splitCur.size() == 1) {
            splitCur.add("0");
            splitCur.add("0");
        } else if (splitCur.size() == 2) {
            splitCur.add("0");
        }

        if (splitCheck.size() == 1) {
            splitCheck.add("0");
            splitCheck.add("0");
        } else if (splitCheck.size() == 2) {
            splitCheck.add("0");
        }

        // Check if the second digit of the version to be checked is higher than the second digit of the current
        // version of MC
        if (Integer.parseInt(splitCheck.get(1).replaceAll("[^\\d.]", "")) > Integer.parseInt(splitCur.get(1)
                .replaceAll("[^\\d.]", ""))) {
            return false;
        }

        // Check if the third digit of the version to be checked is higher than the third digit of the current
        // version of MC
        return Integer.parseInt(splitCheck.get(2).replaceAll("[^\\d.]", "")) <= Integer.parseInt(splitCur.get(2)
                .replaceAll("[^\\d.]", ""));
    }

    /**
     * Create a query to retrieve or send data from or to the database.
     * <br><br>To create a query, provide strings as data points.
     * <br>For example, to retrieve the number of cows a player has killed, use this method like so:
     * <br><br><code>makeQuery("uuid", "uuidOfPlayerHere", "mob", "COW")</code>
     *
     * @param strings an array of strings that represents keys and values.
     * @return a {@link me.staartvin.statz.database.datatype.Query} object that represents a query to the database.
     */
    public static Query makeQuery(final Object... strings) {
        final LinkedHashMap<String, Object> queries = new LinkedHashMap<>();

        for (int i = 0; i < strings.length; i += 2) {
            queries.put(strings[i].toString(), strings[i + 1]);
        }

        Query query = new Query(queries);

        return query;
    }

    public static Query makeQuery(UUID uuid, Object... strings) {

        final LinkedHashMap<String, Object> queries = new LinkedHashMap<>();

        for (int i = 0; i < strings.length; i += 2) {
            queries.put(strings[i].toString(), strings[i + 1]);
        }

        Query query = new Query(queries);
        query.setValue("uuid", uuid.toString());

        return query;
    }

    /**
     * Convert a hashmap query to a string so that it can be used in a SQL
     * query.
     *
     * @param queries HashMap containing the conditions
     * @return a string that represents the conditions 'in SQL style'.
     */
    public static String convertQuery(final Query queries) {
        // Query exists with key and value.
        StringBuilder searchQuery = new StringBuilder();

        for (final Entry<String, Object> query : queries.getEntrySet()) {
            searchQuery.append(query.getKey() + "='" + query.getValue() + "' AND ");
        }

        final int lastIndex = searchQuery.lastIndexOf("AND");
        searchQuery = new StringBuilder(searchQuery.substring(0, lastIndex));

        return searchQuery.toString();
    }

    // Courtesy to Lolmewn for this code.
    @SuppressWarnings("deprecation")
    public static String getMovementType(Player player) {
        if (player.isFlying()) {
            return "FLY";
        }

        if (player.isSwimming()) {
            return "SWIM";
        }

        if (player.isInsideVehicle()) {
            Entity vehicle = player.getVehicle();

            if (vehicle instanceof Boat) {
                return "BOAT";
            } else if (vehicle instanceof Minecart) {

                if (vehicle.getPassenger() != null && vehicle.getPassenger() instanceof Player) {
                    return "MINECART";
                } else if (vehicle.getPassenger() != null && vehicle.getPassenger() instanceof Pig) {
                    return "PIG IN MINECART";
                } else if (vehicle.getPassenger() != null && vehicle.getPassenger() instanceof Horse) {
                    return "HORSE IN MINECART";
                }
            } else if (vehicle instanceof Pig) {
                if (vehicle.isInsideVehicle() && vehicle.getVehicle() instanceof Minecart) {
                    return "PIG IN MINECART";
                } else {
                    return "PIG";
                }
            } else {
                try {
                    if (vehicle instanceof Horse) {
                        return "HORSE";
                    }
                } catch (Exception e) {
                }
            }
        }

        if (isGliding.containsKey(player.getUniqueId()) && isGliding.get(player.getUniqueId())) {
            return "FLY WITH ELYTRA";
        }

        return "WALK"; // Default to walking
    }

    /**
     * Use this method to convert a Query into human-readable form.
     *
     * @param query Query to convert.
     * @return a string that is readable to humans and represents the given query.
     */
    public static String printQuery(HashMap<String, String> query) {
        StringBuilder builder = new StringBuilder("[");

        for (Entry<String, String> entry : query.entrySet()) {
            builder.append(entry.getKey() + " - " + entry.getValue() + ", ");
        }

        builder.append("]");

        return builder.toString();
    }

    /**
     * Round a double to given digits behind the dot (decimal digits).
     *
     * @param value  Value to round
     * @param places decimal digits to round to.
     * @return a double that is rounded to given amount of decimal digits.
     */
    public static double roundDouble(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    /**
     * Create a string that shows all elements of the given list <br>
     * The end divider is the last word used for the second last element. <br>
     * Example: a list with {1,2,3,4,5,6,7,8,9,0} and end divider 'or'. <br>
     * Would show: 1, 2, 3, 4, 5, 6, 7, 8, 9 or 0.
     *
     * @param c          Array to get the elements from.
     * @param endDivider Last word used for dividing the second last and last
     *                   word.
     * @return string with all elements.
     */
    public static String seperateList(final Collection<?> c, final String endDivider) {
        final Object[] array = c.toArray();
        if (array.length == 1)
            return array[0].toString();

        if (array.length == 0)
            return null;

        final StringBuilder string = new StringBuilder();

        for (int i = 0; i < array.length; i++) {

            if (i == (array.length - 1)) {
                string.append(array[i]);
            } else if (i == (array.length - 2)) {
                // Second last
                string.append(array[i] + " " + endDivider + " ");
            } else {
                string.append(array[i] + ", ");
            }
        }

        return string.toString();
    }

    /**
     * Find the closest string in a list compared to a given string.
     *
     * @param input String to compare
     * @param list  List of strings to choose from
     * @return a string from the given list that is most closely related to the given string (first parameter).
     */
    public static String findClosestSuggestion(String input, List<String> list) {
        int lowestDistance = Integer.MAX_VALUE;
        String bestSuggestion = null;

        for (String possibility : list) {
            int dist = editDistance(input, possibility);

            if (dist < lowestDistance) {
                lowestDistance = dist;
                bestSuggestion = possibility;
            }
        }

        return bestSuggestion + ";" + lowestDistance;
    }

    /**
     * Calculates the edit distance of two strings (Levenshtein distance)
     *
     * @param a First string to compare
     * @param b Second string to compate
     * @return Levenshtein distance of two strings.
     */
    public static int editDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    /**
     * Convert an integer to a string. <br>
     * Format of the returned string: <b>x days, y hours, z minutes and r
     * seconds</b>
     *
     * @param count the value to convert
     * @param time  the type of time of the value given (DAYS, HOURS, MINUTES,
     *              SECONDS)
     * @return string in given format
     */
    public static String timeToString(int count, final Time time) {
        final StringBuilder b = new StringBuilder();

        int days = 0, hours = 0, minutes = 0, seconds = 0;

        if (time.equals(Time.DAYS)) {
            days = count;
        } else if (time.equals(Time.HOURS)) {
            days = count / 24;

            hours = count - (days * 24);
        } else if (time.equals(Time.MINUTES)) {
            days = count / 1440;

            count = count - (days * 1440);

            hours = count / 60;

            minutes = count - (hours * 60);
        } else if (time.equals(Time.SECONDS)) {
            days = count / 86400;

            count = count - (days * 86400);

            hours = count / 3600;

            count = count - (hours * 3600);

            minutes = count / 60;

            seconds = count - (minutes * 60);
        }

        if (days != 0) {
            b.append(days);
            b.append(" ");
            if (days != 1)
                b.append("days");
            else
                b.append("day");

            if (hours != 0 || minutes != 0)
                b.append(", ");
        }

        if (hours != 0) {
            b.append(hours);
            b.append(" ");
            if (hours != 1)
                b.append("hours");
            else
                b.append("hour");

            if (minutes != 0)
                b.append(", ");
        }

        if (minutes != 0 || (hours == 0 && days == 0)) {
            b.append(minutes);
            b.append(" ");
            if (minutes != 1)
                b.append("minutes");
            else
                b.append("minute");

            if (seconds != 0)
                b.append(", ");
        }

        if (seconds != 0) {
            b.append(seconds);
            b.append(" ");
            if (seconds != 1)
                b.append("seconds");
            else
                b.append("second");
        }

        // Replace last comma with an and if needed.
        final int index = b.lastIndexOf(",");

        if (index != -1) {
            b.replace(index, index + 1, " " + "and");
        }

        return b.toString();
    }

    public static String getInfoString(Query query, PlayerStat statType, String playerName) {
        StringBuilder builder = new StringBuilder(ChatColor.GOLD + playerName + ChatColor.DARK_AQUA + " ");

        if (statType.equals(PlayerStat.ARROWS_SHOT)) {
            builder.append(createStringWithParams("shot {0} arrows on world '{1}'",
                    (int) query.getValue(), query.getValue("world")));
        } else if (statType.equals(PlayerStat.BLOCKS_BROKEN)) {
            builder.append(createStringWithParams("broke {0} blocks of {1} on world '{2}'.",
                    (int) query.getValue(), query.getValue("block"), query.getValue("world")));
        } else if (statType.equals(PlayerStat.BLOCKS_PLACED)) {
            builder.append(createStringWithParams(
                    "placed {0} blocks of {1} on world '{2}'.", (int) query.getValue(),
                    query.getValue("block"), query.getValue("world")));
        } else if (statType.equals(PlayerStat.BUCKETS_EMPTIED)) {
            builder.append(createStringWithParams("emptied {0} buckets on world '{1}'", (int) query.getValue(),
                    query.getValue("world")));
        } else if (statType.equals(PlayerStat.BUCKETS_FILLED)) {
            builder.append(createStringWithParams("filled {0} buckets on world '{1}'", (int) query.getValue(),
                    query.getValue("world")));
        } else if (statType.equals(PlayerStat.COMMANDS_PERFORMED)) {
            builder.append(createStringWithParams(
                    "performed " + ChatColor.GREEN + "{0}" + ChatColor.DARK_AQUA + " {1} times on world '{2}'",
                    query.getValue("command").toString() + " " + query.getValue("arguments").toString(),
                    (int) query.getValue(), query.getValue("world")));
        } else if (statType.equals(PlayerStat.DAMAGE_TAKEN)) {
            builder.append(createStringWithParams("took {0} points of damage by {1} on world '{2}'",
                    (int) query.getValue(), query.getValue("cause").toString(), query.getValue("world")));
        } else if (statType.equals(PlayerStat.DEATHS)) {
            builder.append(createStringWithParams("died {0} times on world '{1}'", (int) query.getValue(),
                    query.getValue("world")));
        } else if (statType.equals(PlayerStat.DISTANCE_TRAVELLED)) {
            builder.append(createStringWithParams("travelled {0} blocks on world '{1}' by {2}",
                    roundDouble(query.getValue(), 2), query.getValue("world"), query.getValue("moveType")));
        } else if (statType.equals(PlayerStat.EGGS_THROWN)) {
            builder.append(createStringWithParams("has thrown {0} eggs on world '{1}'", (int) query.getValue(),
                    query.getValue("world")));
        } else if (statType.equals(PlayerStat.ENTERED_BEDS)) {
            builder.append(createStringWithParams("has slept {0} times in a bed on world '{1}'", (int) query.getValue(),
                    query.getValue("world")));
        } else if (statType.equals(PlayerStat.FOOD_EATEN)) {
            builder.append(createStringWithParams("has eaten {0} {1} times on world '{2}'", query.getValue("foodEaten"),
                    (int) query.getValue(), query.getValue("world")));
        } else if (statType.equals(PlayerStat.ITEMS_CAUGHT)) {
            builder.append(createStringWithParams("has caught {0} {1} times on world '{2}'", query.getValue("caught"),
                    (int) query.getValue(), query.getValue("world")));
        } else if (statType.equals(PlayerStat.ITEMS_CRAFTED)) {
            builder.append(createStringWithParams("has crafted {0} {1} times on world '{2}'", query.getValue("item"),
                    (int) query.getValue(), query.getValue("world")));
        } else if (statType.equals(PlayerStat.ITEMS_DROPPED)) {
            builder.append(createStringWithParams("has dropped {0} {1} times on world '{2}'", query.getValue("item"),
                    (int) query.getValue(), query.getValue("world")));
        } else if (statType.equals(PlayerStat.ITEMS_PICKED_UP)) {
            builder.append(createStringWithParams("has picked up {0} {1} times on world '{2}'", query.getValue("item"),
                    (int) query.getValue(), query.getValue("world")));
        } else if (statType.equals(PlayerStat.JOINS)) {
            builder.append(createStringWithParams("has joined the server {0} times", (int) query.getValue()));
        } else if (statType.equals(PlayerStat.KILLS_MOBS)) {
            builder.append(createStringWithParams("has killed {0}s {1} times on world '{2}'", query.getValue("mob"),
                    (int) query.getValue(), query.getValue("world")));
        } else if (statType.equals(PlayerStat.KILLS_PLAYERS)) {
            builder.append(createStringWithParams("has killed {0} {1} times on world '{2}'",
                    query.getValue("playerKilled"), (int) query.getValue(), query.getValue("world")));
        } else if (statType.equals(PlayerStat.TELEPORTS)) {
            builder.append(createStringWithParams("has teleported from {0} to {1} {2} times because of {3}",
                    query.getValue("world"), query.getValue("destWorld"), (int) query.getValue(),
                    query.getValue("cause")));
        } else if (statType.equals(PlayerStat.TIME_PLAYED)) {
            builder.append(createStringWithParams("has played for {0} on world '{1}'",
                    StatzUtil.timeToString((int) query.getValue(), Time.MINUTES), query.getValue("world")));
        } else if (statType.equals(PlayerStat.TIMES_KICKED)) {
            builder.append(createStringWithParams("has been kicked {0} times on world '{1}' with reason '{2}'",
                    (int) query.getValue(), query.getValue("world"), query.getValue("reason")));
        } else if (statType.equals(PlayerStat.TIMES_SHORN)) {
            builder.append(createStringWithParams("has shorn {0} sheep on world '{2}'", (int) query.getValue(),
                    query.getValue("world")));
        } else if (statType.equals(PlayerStat.TOOLS_BROKEN)) {
            builder.append(createStringWithParams("has broken {0} {1} times on world '{2}'", query.getValue("item"),
                    (int) query.getValue(), query.getValue("world")));
        } else if (statType.equals(PlayerStat.VILLAGER_TRADES)) {
            builder.append(createStringWithParams("has traded with {0} villagers on world '{1}' for item {2}",
                    (int) query.getValue(), query.getValue("world"), query.getValue("trade")));
        } else if (statType.equals(PlayerStat.VOTES)) {
            builder.append(createStringWithParams("has voted {0} times", (int) query.getValue()));
        } else if (statType.equals(PlayerStat.WORLDS_CHANGED)) {
            builder.append(createStringWithParams("has changed from {0} to {1} {2} times", query.getValue("world"),
                    query.getValue("destWorld"), (int) query.getValue()));
        } else if (statType.equals(PlayerStat.XP_GAINED)) {
            builder.append(createStringWithParams("has gained {0} points of xp on world '{1}'", (int) query.getValue(),
                    query.getValue("world")));
        }

        return builder.toString();
    }

    public static String createStringWithParams(String fullString, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            fullString = fullString.replace("{" + i + "}", objects[i].toString());
        }

        return fullString;
    }

    public static String getMinecraftVersion() {
        String version = Bukkit.getBukkitVersion();

        String[] split = version.split("-");

        if (split.length < 1) {
            return "Unknown";
        }

        return split[0];
    }

    public static String getMobType(Entity e) {
        String mobType = e.getType().toString();

        if (e instanceof Skeleton) {
            final Skeleton ske = (Skeleton) e;

            // We don't have to check for WITHER_SKELETON OR STRAY anymore
        } else if (e instanceof Creeper) {
            final Creeper cre = (Creeper) e;

            if (cre.isPowered()) {
                mobType = "POWERED " + mobType;
            }
        } else if (e instanceof Chicken) {
            final Chicken mob = (Chicken) e;

            if (mob.getPassenger() != null) {
                mobType = mobType + " JOCKEY";
            }
        } else if (/* Check for Minecraft version */ StatzUtil.isHigherVersion("1.8") && e instanceof Rabbit) {
            final Rabbit mob = (Rabbit) e;

            if (mob.getRabbitType() == Type.THE_KILLER_BUNNY) {
                mobType = "KILLER " + mobType;
            }
        } else if (e instanceof Spider) {
            final Spider mob = (Spider) e;

            if (mob.getPassenger() != null) {
                mobType = mobType + " JOCKEY";
            }
        } else if (/* Check for Minecraft version */ StatzUtil.isHigherVersion("1.8") && e instanceof Guardian) {
            final Guardian mob = (Guardian) e;

            // We don't have to check for ELDER_GUARDIAN anymore
        } else if (e instanceof Zombie) {
            Zombie mob = (Zombie) e;
            // We don't have to check for HUSK anymore
        }

        return mobType;
    }

    /**
     * Convert a string to an integer.
     *
     * @param string input; this must be in the format '10d 14h 15m'
     * @param time   the time type of the output
     * @return the integer representing the number of seconds/minutes/hours/days
     */
    public static int stringToTime(String string, final Time time) {
        int res = 0;

        string = string.trim();

        final Pattern pattern = Pattern.compile("((\\d+)d)?((\\d+)h)?((\\d+)m)?");
        final Matcher matcher = pattern.matcher(string);

        matcher.find();

        final String days = matcher.group(2);
        final String hours = matcher.group(4);
        String minutes = matcher.group(6);

        // No day or hours or minute was given, so default to minutes.
        if (days == null && hours == null & minutes == null) {
            minutes = string;
        }

        res += stringtoDouble(minutes);
        res += stringtoDouble(hours) * 60;
        res += stringtoDouble(days) * 60 * 24;

        // Res time is in minutes

        if (time.equals(Time.SECONDS)) {
            return res * 60;
        } else if (time.equals(Time.MINUTES)) {
            return res;
        } else if (time.equals(Time.HOURS)) {
            return res / 60;
        } else if (time.equals(Time.DAYS)) {
            return res / 1440;
        } else {
            return 0;
        }
    }

    public static double stringtoDouble(final String string) throws NumberFormatException {
        double res = 0;

        if (string != null)

            res = Double.parseDouble(string);

        return res;
    }


    /**
     * Get the matching material from an item id and damage value. Attempts to find the material that matches the
     * given data type. This method is to be used with old (pre 1.13) item id and data values. It converts the item
     * id and data value pair into the new (post 1.13) material.
     *
     * @param typeId    Id of item
     * @param dataValue optional data value
     * @return the material that matches the given item id and data value, or null if there is no match.
     */
    public static org.bukkit.Material findMaterial(int typeId, int dataValue) {
        for (Material mat : EnumSet.allOf(Material.class)) {
            XMaterial matchedMaterial = XMaterial.matchXMaterial(typeId, (byte) dataValue);

            if (matchedMaterial == null) {
                return null;
            }

            return matchedMaterial.parseMaterial();
        }

        return null;
    }

    public static boolean isHarvestableCrop(Material material) {

        List<Material> crops = Arrays.asList(WHEAT, BEETROOTS, CARROTS, POTATOES, MELON, PUMPKIN, BAMBOO, COCOA,
                SUGAR_CANE, CACTUS, BROWN_MUSHROOM, RED_MUSHROOM, KELP_PLANT,
                SEA_PICKLE, NETHER_WART, CHORUS_PLANT, CHORUS_FLOWER);

        return crops.contains(material);
    }

    public enum Time {
        DAYS, HOURS, MINUTES, SECONDS
    }
}
