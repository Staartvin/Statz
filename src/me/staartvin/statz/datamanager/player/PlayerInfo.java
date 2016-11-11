package me.staartvin.statz.datamanager.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.util.StatzUtil;

/**
 * Statistics about a player recorded by Statz
 * <p>
 * Date created: 15:07:07
 * 17 apr. 2016
 * 
 * @author "Staartvin"
 *
 */
public class PlayerInfo {

	private UUID uuid;
	private boolean isValid;

	private List<Query> results = new ArrayList<>();

	public PlayerInfo(final UUID uuid) {
		this.setUUID(uuid);
	}

	/**
	 * Get UUID of the player this PlayerInfo represents.
	 * @return uuid of the player
	 */
	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(final UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Checks whether this PlayerInfo is valid. PlayerInfo is considered valid
	 * when all of these requirements are met:
	 * <ul>
	 * <li>The data is not corrupt.
	 * <li>The query was valid and did not give any errors.
	 * <li>The requested data is stored about this player
	 * </ul>
	 * 
	 * @return true if valid info, false otherwise.
	 */
	public boolean isValid() {
		return isValid;
	}

	public void setValid(final boolean isValid) {
		this.isValid = isValid;
	}

	/**
	 * Get the value of a column in the given query.
	 * @param map Query to look for the given key
	 * @param key Key to search value for
	 * @return value of the given key in the given query or null if it doesn't exist.
	 */
	public String getValue(Query map, final String key) {
		return map.getValue(key).toString();
	}

	/**
	 * Get a list of hashmaps that contain the data that is stored in the database. 
	 * <br>Every hashmap in the list represents one row in the database.
	 * <br>Each row contains keys. Each key is the column name of the specific dataset in the database.
	 * <br>To get the value of a column from a specific row, take these steps:
	 * <br>
	 * <li>First get a specific row. Since lists are zero-based, the first row would be 
	 * <br>{@code HashMap map = getResults().get(0)}</li>
	 * <li>Secondly, to get the value of a column name, we need to check if it exists:
	 * <br>{@code if (map.get("world") != null)}</li>
	 * <li>Lastly, since map.get() returns an Object, it is recommended to convert it to a primitive type (or string)</li>
	 * @return a list of hashmaps containing the data in the database of this specific player.
	 */
	public List<Query> getResults() {
		return results;
	}
	
	/**
	 * Get a certain row of the returned results.
	 * See {@link #getResults()} for more information.
	 * @param rowNumber Row number to get query of.
	 * @return a query that corresponds to this row, or null if it doesn't exist.
	 */
	public Query getRow(int rowNumber) {
		if (rowNumber < 0 || rowNumber >= this.getResults().size()) {
			return null;
		}
		
		return results.get(rowNumber);
	}
	
	/**
	 * Get the value of a given column of a given row.
	 * @param rowNumber Row number to get the query of.
	 * @param columnName Name of the column to get info out of the row.
	 * @return a value that corresponds to the given value in the returned results or null if nothing was found.
	 */
	public Object getValue(int rowNumber, String columnName) {
		Query row = this.getRow(rowNumber);
		
		if (row == null) return null;
		
		return row.getValue(columnName);
	}
	
	/**
	 * Get the sum of all values in the 'value' of each row that meets the given RowRequirements.
	 * @see RowRequirement RowRequirement class for more info about requirements and some examples.
	 * @param reqs A list of requirements that need to be met before adding the value to the sum.
	 * @return the sum of the values in the rows that meet the given requirement or 0 if results were invalid or non-existent.
	 */
	public double getTotalValue(RowRequirement... reqs) {
		// Check if we have any requirements - if not, just return double value.
		if (reqs == null || reqs.length == 0) {
			return this.getTotalValue();
		}
		
		double value = 0;

		List<Query> results = this.getResults();

		if (results == null || results.isEmpty() || !this.isValid())
			return value;

		for (Query result : results) {
			boolean isValid = true;

			for (int i = 0; i < reqs.length; i++) {

				RowRequirement req = reqs[i];
				// Check if each condition that was given is true.
				if (result.getValue(req.getColumnName()) == null
						|| !result.getValue(req.getColumnName()).toString().equalsIgnoreCase(req.getColumnValue())) {
					isValid = false;
					break;
				}
			}

			// All conditions were met, so we add this value.
			if (isValid) {
				value += Double.parseDouble(result.getValue("value").toString());
			}
		}

		return value;
	}

	public void setResults(List<Query> result) {
		this.results = result;
	}

	public void addResult(Query map) {
		this.results.add(map);
	}

	public void removeResult(Query map) {
		this.results.remove(map);
	}

	/**
	 * Get the total value of the 'value' column. This method sums up all the values from the 'value' column in each row.
	 * @return the sum of the values of each row.
	 */
	public double getTotalValue() {
		double value = 0;

		for (Query q : results) {
			value += q.getValue();
		}

		return value;
	}
	
	/**
	 * Get the total value but round to given decimal places.
	 * @param roundedDecimals How many decimal places to round to.
	 * @return the same as {@link #getTotalValue()}, but rounded to given decimal places.
	 */
	public double getTotalValue(int roundedDecimals) {
		double value = getTotalValue();
		
		return StatzUtil.roundDouble(value, roundedDecimals);
	}
	
	@Override
	public String toString() {
		StringBuilder endString = new StringBuilder("PlayerInfo of " + this.getUUID() + ": {");
		
		for (Query q : this.results) {
			endString.append(q.toString() + ", ");
		}
		
		int lastComma = endString.lastIndexOf(",");
		
		if (lastComma >= 0) {
			endString.deleteCharAt(lastComma);
		}
		
		endString = new StringBuilder(endString.toString().trim());
		
		endString.append("}");
		
		return endString.toString();
	}

}
