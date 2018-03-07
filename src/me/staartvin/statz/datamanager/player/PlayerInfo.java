package me.staartvin.statz.datamanager.player;

import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.database.datatype.RowRequirement;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.util.StatzUtil;

import java.util.*;

/**
 * Statistics of a player recorded by Statz
 * <br>
 * <br>
 * Statistics of a player are stored in (key, value) pairs where a key is a PlayerStat object and a value is a list
 * of queries that represent the data retrieved from the database. Each {@link Query} object represents one row in
 * the database.
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

    private Map<PlayerStat, List<Query>> statistics = new HashMap<>();

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
     * Get data for a certain statistic of a player. It returns a list of {@link Query} objects that represent the
     * data in the database. Note that each Query object represents one row in the database.
     *
     * @param statType Type of statistic to get data for.
     * @return A list of {@link Query} objects that represent the rows in the database. If there is no data for the
     * given type of statistic, an empty list will be returned.
	 */
    public List<Query> getDataOfPlayerStat(PlayerStat statType) {
        if (this.hasDataOfPlayerStat(statType)) {
            return new ArrayList<>();
        }

        return statistics.get(statType);
	}
	
	/**
     * Get a row of a specific dataset.
     * See {@link #getDataOfPlayerStat(PlayerStat)} for more information.
     * @param statType Type of statistic to get data for.
	 * @param rowNumber Row number to get query of.
     * @return a Query that corresponds to this row, or null if it doesn't exist.
	 */
    public Query getRow(PlayerStat statType, int rowNumber) {
        List<Query> rows = this.getDataOfPlayerStat(statType);

        if (rowNumber < 0 || rowNumber >= rows.size()) {
			return null;
		}

        return rows.get(rowNumber);
    }

    /**
     * Check whether data for a given statistic is available.
     *
     * @param statType Type of statistic to check.
     *
     * @return true if the data is available, false otherwise.
     */
    public boolean hasDataOfPlayerStat(PlayerStat statType) {
        return statistics.containsKey(statType) && statistics.get(statType) != null;
    }
	
	/**
	 * Get the value of a given column of a given row.
     * @param statType Type of statistic to get data for.
     * @param rowNumber Row number to get the query of.
	 * @param columnName Name of the column to get info out of the row.
	 * @return a value that corresponds to the given value in the returned results or null if nothing was found.
     */
    public Object getValue(PlayerStat statType, int rowNumber, String columnName) {
        Query row = this.getRow(statType, rowNumber);
		
		if (row == null) return null;
		
		return row.getValue(columnName);
    }

    /**
     * Get the sum of all values in the 'value' column of each row that meets the given RowRequirements.
     * See the {@link RowRequirement} class for more info about requirements and some examples.
     * @param statType Type of statistics to get data for.
	 * @param reqs A list of requirements that need to be met before adding the value to the sum.
	 * @return the sum of the values in the rows that meet the given requirement or 0 if results were invalid or non-existent.
     */
    public double getTotalValue(PlayerStat statType, RowRequirement... reqs) {
		// Check if we have any requirements - if not, just return double value.
		if (reqs == null || reqs.length == 0) {
            return this.getTotalValue(statType);
		}
		
		double value = 0;

        List<Query> rows = this.getDataOfPlayerStat(statType);

        if (rows.isEmpty() || !this.isValid())
			return value;

        for (Query row : rows) {
			boolean isValid = true;

			for (int i = 0; i < reqs.length; i++) {

				RowRequirement req = reqs[i];
				// Check if each condition that was given is true.
                if (row.getValue(req.getColumnName()) == null
                        || !row.getValue(req.getColumnName()).toString().equalsIgnoreCase(req.getColumnValue())) {
					isValid = false;
					break;
				}
			}

			// All conditions were met, so we add this value.
			if (isValid) {
                value += row.getDoubleValue("value");
			}
		}

		return value;
	}

    /**
     * Set the data for a specific statistic.
     *
     * @param statType Type of statistic
     * @param rows     Data to set
     *
     * @throws IllegalArgumentException if statType or rows is invalid.
     */
    public void setData(PlayerStat statType, List<Query> rows) throws IllegalArgumentException {

        if (statType == null) {
            throw new IllegalArgumentException("Stat cannot be null.");
        }

        if (rows == null) {
            throw new IllegalArgumentException("Given rows cannot be null");
        }

        statistics.put(statType, rows);
    }

    /**
     * Add a row to data of a specific statistic.
     *
     * @param statType Type of statistic.
     * @param row      Row to add.
     *
     * @throws IllegalArgumentException if statistic is null or the row is null.
     */
    public void addRow(PlayerStat statType, Query row) throws IllegalArgumentException {

        if (statType == null) {
            throw new IllegalArgumentException("Stat cannot be null.");
        }

        if (row == null) {
            throw new IllegalArgumentException("Row cannot be null");
        }

        List<Query> rows = this.getDataOfPlayerStat(statType);

        rows.add(row);

        this.setData(statType, rows);
    }

    /**
     * Remove a row from data of a specific statistic.
     *
     * @param statType Type of statistic.
     * @param row      Row to remove.
     *
     * @throws IllegalArgumentException if statistic is null or row is null.
     */
    public void removeResult(PlayerStat statType, Query row) throws IllegalArgumentException {
        if (statType == null) {
            throw new IllegalArgumentException("Stat cannot be null");
        }

        if (row == null) {
            throw new IllegalArgumentException("Row cannot be null");
        }

        // No row to be removed
        if (!this.hasDataOfPlayerStat(statType)) {
            return;
        }

        List<Query> rows = this.getDataOfPlayerStat(statType);

        rows.remove(row);

        this.setData(statType, rows);
    }

	/**
	 * Get the total value of the 'value' column. This method sums up all the values from the 'value' column in each row.
	 * @return the sum of the values of each row.
     */
    public double getTotalValue(PlayerStat statType) {
		double value = 0;

        for (Query q : this.getDataOfPlayerStat(statType)) {
			value += q.getValue();
		}

		return value;
	}
	
	/**
	 * Get the total value but round to given decimal places.
	 * @param roundedDecimals How many decimal places to round to.
     * @return the same as {@link #getTotalValue(PlayerStat)}, but rounded to given decimal places.
     */
    public double getTotalValue(PlayerStat statType, int roundedDecimals) {
		double value = getTotalValue(statType);
		
		return StatzUtil.roundDouble(value, roundedDecimals);
	}
	
	@Override
	public String toString() {
		StringBuilder endString = new StringBuilder("PlayerInfo of " + this.getUUID() + ": {");

        StringBuilder queryString;

        for (Map.Entry<PlayerStat, List<Query>> entry : statistics.entrySet()) {
            PlayerStat statType = entry.getKey();
            List<Query> queries = entry.getValue();

            queryString = new StringBuilder(statType + ": {");

            for (Query q : queries) {
                queryString.append(q.toString() + ", ");
                System.out.println(queryString.toString());
            }

            int lastComma = queryString.lastIndexOf(",");

            if (lastComma >= 0) {
                queryString.deleteCharAt(lastComma);
            }

            queryString.append("}, ");
            System.out.println("QUERYYSTRING END: " + queryString.toString().trim());
            endString.append(queryString.toString().trim());
        }
		
		endString = new StringBuilder(endString.toString().trim());
		
		endString.append("}");
		
		return endString.toString();
	}

}
