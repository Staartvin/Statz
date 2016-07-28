package me.staartvin.statz.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.PlayerStat;
import net.md_5.bungee.api.ChatColor;

public class StatzUtil {

	public static enum Time {
		DAYS, HOURS, MINUTES, SECONDS
	}

	public static Query makeQuery(final Object... strings) {
		final LinkedHashMap<String, String> queries = new LinkedHashMap<>();

		for (int i = 0; i < strings.length; i += 2) {
			queries.put(strings[i].toString(), strings[i + 1].toString());
		}

		Query query = new Query(queries);

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
		StringBuilder searchQuery = new StringBuilder("");

		for (final Entry<String, String> query : queries.getEntrySet()) {
			searchQuery.append(query.getKey() + "='" + query.getValue() + "' AND ");
		}

		final int lastIndex = searchQuery.lastIndexOf("AND");
		searchQuery = new StringBuilder(searchQuery.substring(0, lastIndex));

		return searchQuery.toString();
	}

	/**
	 * Get the name of this food item.
	 * 
	 * @param item ItemStack to get the name of.
	 * @return Name of food, or null if not a valid food item.
	 */
	public static String getFoodName(final ItemStack item) {
		// Returns null if not a valid food item
		// Got Materials from https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html

		if (item == null)
			return null;

		switch (item.getType()) {
			case APPLE:
				return "APPLE";
			case BAKED_POTATO:
				return "BAKED_POTATO";
			case BREAD:
				return "BREAD";
			case CAKE_BLOCK: // not working atm
				return "CAKE_BLOCK";
			case CARROT_ITEM:
				return "CARROT_ITEM";
			case COOKED_CHICKEN:
				return "COOKED_CHICKEN";
			case COOKED_FISH: {
				if (item.getDurability() == (short) 1) {
					return "COOKED_SALMON";
				}
				return "COOKED_FISH";
			}
			case COOKED_MUTTON:
				return "COOKED_MUTTON";
			case GRILLED_PORK:
				return "GRILLED_PORK";
			case COOKED_RABBIT:
				return "COOKED_RABBIT";
			case COOKIE:
				return "COOKIE";
			case GOLDEN_APPLE: {
				if (item.getDurability() == (short) 1) {
					return "ENCHANTED_GOLDEN_APPLE";
				}
				return "GOLDEN_APPLE";
			}
			case GOLDEN_CARROT:
				return "GOLDEN_CARROT";
			case MELON:
				return "MELON";
			case MUSHROOM_SOUP:
				return "MUSHROOM_SOUP";
			case RABBIT_STEW:
				return "RABBIT_STEW";
			case RAW_BEEF:
				return "RAW_BEEF";
			case RAW_CHICKEN:
				return "RAW_CHICKEN";
			case RAW_FISH: {
				if (item.getDurability() == (short) 1) {
					return "RAW_SALMON";
				} else if (item.getDurability() == (short) 2) {
					return "CLOWNFISH";
				} else if (item.getDurability() == (short) 3) {
					return "PUFFERFISH";
				}
				return "RAW_FISH";
			}
			case POISONOUS_POTATO:
				return "POISONOUS_POTATO";
			case POTATO:
				return "POTATO";
			case PUMPKIN_PIE:
				return "PUMPKIN_PIE";
			case MUTTON:
				return "MUTTON"; // raw
			case COOKED_BEEF:
				return "COOKED_BEEF";
			case RABBIT:
				return "RABBIT";
			case ROTTEN_FLESH:
				return "ROTTEN_FLESH";
			case SPIDER_EYE:
				return "SPIDER_EYE";
			default:
				return item.getType().toString().toUpperCase();
		}
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getFoodItemFromName(String name) {
		// Cannot use switch, is only supported in Java 1.7+

		if (name == null)
			return null;

		name = name.toUpperCase();
		name = name.replace(" ", "_");

		if (name.equals("APPLE")) {
			return new ItemStack(Material.APPLE, 1);
		} else if (name.equals("BAKED_POTATO")) {
			return new ItemStack(Material.BAKED_POTATO, 1);
		} else if (name.equals("BREAD")) {
			return new ItemStack(Material.BREAD, 1);
		} else if (name.equals("CAKE_BLOCK")) {
			return new ItemStack(Material.CAKE_BLOCK, 1);
		} else if (name.equals("CARROT_ITEM")) {
			return new ItemStack(Material.CARROT_ITEM, 1);
		} else if (name.equals("COOKED_CHICKEN")) {
			return new ItemStack(Material.COOKED_CHICKEN, 1);
		} else if (name.equals("COOKED_FISH")) {
			return new ItemStack(Material.COOKED_FISH, 1);
		} else if (name.equals("COOKED_SALMON")) {
			return new ItemStack(Material.COOKED_FISH.getId(), 1, (short) 1);
		} else if (name.equals("COOKED_MUTTON")) {
			return new ItemStack(Material.COOKED_MUTTON, 1);
		} else if (name.equals("GRILLED_PORK")) {
			return new ItemStack(Material.GRILLED_PORK, 1);
		} else if (name.equals("COOKED_RABBIT")) {
			return new ItemStack(Material.COOKED_RABBIT, 1);
		} else if (name.equals("COOKIE")) {
			return new ItemStack(Material.COOKIE, 1);
		} else if (name.equals("GOLDEN_APPLE")) {
			return new ItemStack(Material.GOLDEN_APPLE, 1);
		} else if (name.equals("ENCHANTED_GOLDEN_APPLE")) {
			return new ItemStack(Material.GOLDEN_APPLE.getId(), 1, (short) 1);
		} else if (name.equals("GOLDEN_CARROT")) {
			return new ItemStack(Material.GOLDEN_CARROT, 1);
		} else if (name.equals("MELON")) {
			return new ItemStack(Material.MELON, 1);
		} else if (name.equals("MUSHROOM_SOUP")) {
			return new ItemStack(Material.MUSHROOM_SOUP, 1);
		} else if (name.equals("RABBIT_STEW")) {
			return new ItemStack(Material.RABBIT_STEW, 1);
		} else if (name.equals("RAW_BEEF")) {
			return new ItemStack(Material.RAW_BEEF, 1);
		} else if (name.equals("RAW_CHICKEN")) {
			return new ItemStack(Material.RAW_CHICKEN, 1);
		} else if (name.equals("RAW_FISH")) {
			return new ItemStack(Material.RAW_FISH, 1);
		} else if (name.equals("RAW_SALMON")) {
			return new ItemStack(Material.RAW_FISH.getId(), 1, (short) 1);
		} else if (name.equals("CLOWNFISH")) {
			return new ItemStack(Material.RAW_FISH.getId(), 1, (short) 2);
		} else if (name.equals("PUFFERFISH")) {
			return new ItemStack(Material.RAW_FISH.getId(), 1, (short) 3);
		} else if (name.equals("POISONOUS_POTATO")) {
			return new ItemStack(Material.POISONOUS_POTATO, 1);
		} else if (name.equals("POTATO")) {
			return new ItemStack(Material.POTATO, 1);
		} else if (name.equals("PUMPKIN_PIE")) {
			return new ItemStack(Material.PUMPKIN_PIE, 1);
		} else if (name.equals("MUTTON")) {
			return new ItemStack(Material.MUTTON, 1);
		} else if (name.equals("COOKED_BEEF")) {
			return new ItemStack(Material.COOKED_BEEF, 1);
		} else if (name.equals("RABBIT")) {
			return new ItemStack(Material.RABBIT, 1);
		} else if (name.equals("ROTTEN_FLESH")) {
			return new ItemStack(Material.ROTTEN_FLESH, 1);
		} else if (name.equals("SPIDER_EYE")) {
			return new ItemStack(Material.SPIDER_EYE, 1);
		} else
			return null;
	}

	// Courtesy to Lolmewn for this code.
	public static String getMovementType(Player player) {
		if (player.isFlying()) {
			return "FLY";
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
		return "WALK"; // Default to walking
	}

	public static String printQuery(HashMap<String, String> query) {
		StringBuilder builder = new StringBuilder("[");

		for (Entry<String, String> entry : query.entrySet()) {
			builder.append(entry.getKey() + " - " + entry.getValue() + ", ");
		}

		builder.append("]");

		return builder.toString();
	}

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
	 * @param c Array to get the elements from.
	 * @param endDivider Last word used for dividing the second last and last
	 *            word.
	 * @return string with all elements.
	 */
	public static String seperateList(final Collection<?> c, final String endDivider) {
		final Object[] array = c.toArray();
		if (array.length == 1)
			return array[0].toString();

		if (array.length == 0)
			return null;

		final StringBuilder string = new StringBuilder("");

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
	 * @param time the type of time of the value given (DAYS, HOURS, MINUTES,
	 *            SECONDS)
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
			builder.append(createStringWithParams("shot {0} arrows with a force of {1} on world '{2}'",
					(int) query.getValue(), roundDouble(Double.parseDouble(query.getValue("force").toString()), 2),
					query.getValue("world")));
		} else if (statType.equals(PlayerStat.BLOCKS_BROKEN)) {
			builder.append(createStringWithParams("broke {0} blocks of item id {1} and damage value {2} on world '{3}'",
					(int) query.getValue(), query.getIntValue("typeid"), query.getIntValue("datavalue"),
					query.getValue("world")));
		} else if (statType.equals(PlayerStat.BLOCKS_PLACED)) {
			builder.append(createStringWithParams(
					"placed {0} blocks of item id {1} and damage value {2} on world '{3}'", (int) query.getValue(),
					query.getIntValue("typeid"), query.getIntValue("datavalue"), query.getValue("world")));
		} else if (statType.equals(PlayerStat.BUCKETS_EMPTIED)) {
			builder.append(createStringWithParams("emptied {0} buckets on world '{1}'", (int) query.getValue(),
					query.getValue("world")));
		} else if (statType.equals(PlayerStat.BUCKETS_FILLED)) {
			builder.append(createStringWithParams("filled {0} buckets on world '{1}'", (int) query.getValue(),
					query.getValue("world")));
		} else if (statType.equals(PlayerStat.COMMANDS_PERFORMED)) {
			builder.append(createStringWithParams("performed " + ChatColor.GREEN + "{0}" + ChatColor.DARK_AQUA + " {1} times on world '{2}'",
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
}
