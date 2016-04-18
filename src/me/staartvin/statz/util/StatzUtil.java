package me.staartvin.statz.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

public class StatzUtil {

	public static LinkedHashMap<String, String> makeQuery(final Object... strings) {
		final LinkedHashMap<String, String> queries = new LinkedHashMap<>();

		for (int i = 0; i < strings.length; i += 2) {
			queries.put(strings[i].toString(), strings[i + 1].toString());
		}

		return queries;
	}

	/**
	 * Convert a hashmap query to a string so that it can be used in a SQL
	 * query.
	 * 
	 * @param queries HashMap containing the conditions
	 * @return a string that represents the conditions 'in SQL style'.
	 */
	public static String convertQuery(final HashMap<String, String> queries) {
		// Query exists with key and value.
		StringBuilder searchQuery = new StringBuilder("");

		for (final Entry<String, String> query : queries.entrySet()) {
			searchQuery.append(query.getKey() + "='" + query.getValue() + "' AND ");
		}

		final int lastIndex = searchQuery.lastIndexOf("AND");
		searchQuery = new StringBuilder(searchQuery.substring(0, lastIndex));

		return searchQuery.toString();
	}
	
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
			return null;
		}
	}
}
