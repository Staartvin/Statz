package me.staartvin.statz.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class StatzUtil {

	public static LinkedHashMap<String, String> makeQuery(final String... strings) {
		final LinkedHashMap<String, String> queries = new LinkedHashMap<>();

		for (int i = 0; i < strings.length; i += 2) {
			queries.put(strings[i], strings[i + 1]);
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
}
