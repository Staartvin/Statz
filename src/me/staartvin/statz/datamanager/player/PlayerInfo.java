package me.staartvin.statz.datamanager.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.staartvin.statz.database.datatype.Query;
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

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(final UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Checks whether this player info is valid. PlayerInfo is considered valid
	 * when:
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

}
