package me.staartvin.statz.database.datatype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Query {

	private HashMap<String, String> data = new HashMap<String, String>();

	public Query(HashMap<String, String> data) {
		this.setData(data);
	}

	public HashMap<String, String> getData() {
		return data;
	}

	public void setData(HashMap<String, String> data) {
		this.data = data;
	}
	
	public Object getValue(String columnName) {
		return data.get(columnName);
	}
	
	public double getValue() {
		String value = data.get("value");
		
		if (value == null) return -1;
		
		return Double.parseDouble(value);
	}
	
	public boolean hasValue(String columnName) {
		return data.containsKey(columnName) && data.get(columnName) != null;
	}
	
	public Set<Entry<String, String>> getEntrySet() {
		return data.entrySet();
	}
	
	public void setValue(String columnName, Object columnValue) {
		data.put(columnName, columnValue.toString());
	}
	
	/**
	 * Get queries in the given list of queries that conflict with the given query.
	 * A query conflicts with another query when they have the same values for the same columns (except for the column 'value').
	 * <br>
	 * <br>Let's assume there are three queries: A, B and C.
	 * Query A consists of {uuid: Staartvin, mob: COW, world: testWorld}. Query B is made of {uuid: Staartvin, mob:COW, world: noTestWorld}.
	 * Lastly, Query C is made of {uuid: BuilderGuy, mob:COW, world: testWorld}. None of these queries conflict with each other.
	 * <br><br>Query A does not conflict with B, as the world columns do not have the same values. Query A does also not conflict with C,
	 * since the UUIDs don't match. Lastly, query B does not conflict with query C, since both the UUID and world columns do not match.
	 *
	 * @param queries A list of queries to check whether they conflict with the given queryCompare. 
	 * @return a list of queries (from the given queries list) that conflict with this query or null if no conflicts were found.
	 */
	public List<Query> findConflicts(List<Query> queries) {
		List<Query> conflictingQueries = new ArrayList<>();
		
		if (queries == null)
			return null;

		// Do reverse traversal
		for (int i = queries.size() - 1; i >= 0; i--) {

			Query comparedQuery = queries.get(i);

			boolean isSame = true;

			for (Entry<String, String> entry : data.entrySet()) {
				String columnName = entry.getKey();
				String columnValue = entry.getValue();

				if (columnName.equalsIgnoreCase("value")) {
					continue;
				}

				// Stored query does not have value that the given query has -> this cannot conflict
				if (!comparedQuery.hasValue(columnName)) {
					isSame = false;
					break;
				}

				// If value of condition in stored query is not the same as the given query, they cannot conflict. 
				if (!comparedQuery.getValue(columnName).toString().equalsIgnoreCase(columnValue)) {
					isSame = false;
					break;
				}
			}

			if (!comparedQuery.getValue("uuid").toString().equalsIgnoreCase(data.get("uuid"))) {
				isSame = false;
			}

			// We have found a conflicting query
			if (isSame) {
				conflictingQueries.add(comparedQuery);
			}
		}

		// No conflicting query found
		if (conflictingQueries.isEmpty()) {
			return null;
		} else {
			return conflictingQueries;
		}
	}
	
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder("{");
		
		for (Entry<String, String> entry : data.entrySet()) {
			builder.append(entry.getKey() + ": " + entry.getValue() + ", ");
		}
		
		builder.deleteCharAt(builder.length() - 1); // Remove last comma
		
		builder.append("}");
		
		return builder.toString();
	}
}
