package me.staartvin.statz.database.datatype;

import java.util.*;
import java.util.Map.Entry;

/**
 * This class represents a query that is sent or retrieved from the database of Statz.
 * <br>Each query consists of a single row with key-value pairs that hold information.
 * <br>Each key is the column name in the database and the corresponding value is the value of that column in the
 * database.
 *
 * @author Staartvin
 */
public class Query {

    private Map<String, String> data = new HashMap<String, String>();

    public Query(Map<String, String> data) {
        this.setData(data);
    }

    public Query() {
        // Empty constructor
    }

    /**
     * Get the data in the form of a Map object.
     * The keys are the columns and the values are the column values.
     *
     * @return data of this query.
     */
    public Map<String, String> getData() {
        return data;
    }

    /**
     * Set data of this query. Provide a Map object where the keys are the columns and the values are the values of
     * the columns.
     *
     * @param data Map that represents data of the query.
     */
    public void setData(Map<String, String> data) {
        this.data = new HashMap<>();

        // Make sure that keys are lowercase.
        for (Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            setValue(key.toLowerCase(), value);
        }
    }

    /**
     * Get the value of the column name in this query.
     *
     * @param columnName Name of the column to get info of.
     * @return value of the column of this query. Null if the query does not have this info.
     */
    public Object getValue(String columnName) {
        if (!hasColumn(columnName))
            return null;
        return data.get(columnName.toLowerCase());
    }

    /**
     * Get the value of a column as an integer.
     * See {@link Query#getValue(String columnName)}.
     *
     * @param columnName Name of the column
     * @return integer value of this column or 0 if there is no value.
     */
    public int getIntValue(String columnName) {

        Object value = getValue(columnName);

        if (value == null)
            return 0;

        return Integer.parseInt(value.toString());
    }

    /**
     * Get the value of a column as a double.
     * See {@link Query#getValue(String columnName)}.
     *
     * @param columnName Name of the column
     * @return double value of this column of 0 if there is no value.
     */
    public Double getDoubleValue(String columnName) {
        Object value = getValue(columnName);

        if (value == null)
            return 0.0;

        return Double.parseDouble(value.toString());
    }

    /**
     * Get the value of the 'value' column.
     *
     * @return the value of the 'value' colum of this query or 0 if the value was null.
     */
    public double getValue() {
        Object value = this.getValue("value");

        if (value == null)
            return 0;

        return Double.parseDouble(value.toString());
    }

    /**
     * Check to see if this query contains a column with the given column name.
     *
     * @param columnName Column name to check
     * @return true if this query contains the column and the value is not null. False otherwise.
     */
    public boolean hasColumn(String columnName) {
        return data.containsKey(columnName.toLowerCase()) && data.get(columnName.toLowerCase()) != null;
    }

    /**
     * Check to see if this query has a certain value. This method checks all key-value pairs and verifies whether there
     * is a value of a pair that matches the given value.
     *
     * @param value Value to check if it exists in this query.
     * @return true if it exists, false otherwise.
     */
    public boolean hasValue(Object value) {
        if (value == null)
            return false;

        for (Entry<String, String> dataString : data.entrySet()) {
            if (dataString != null && dataString.getValue().equalsIgnoreCase(value.toString())) {
                return true;
            }
        }

        return false;
    }

    public Set<Entry<String, String>> getEntrySet() {
        return data.entrySet();
    }

    /**
     * Set value of a column. If the column does not exist, it is created.
     *
     * @param columnName  Name of the column
     * @param columnValue Value to set
     */
    public void setValue(String columnName, Object columnValue) {
        data.put(columnName.toLowerCase(), columnValue.toString());
    }

    /**
     * Get queries in the given list of queries that conflict with this query.
     * A query conflicts with another query when they have the same values for the same columns (except for the
     * column 'value').
     * <br>
     * <br>Let's assume there are three queries: A, B and C.
     * Query A consists of {uuid: Staartvin, mob: COW, world: testWorld}. Query B is made of {uuid: Staartvin,
     * mob:COW, world: noTestWorld}.
     * Lastly, Query C is made of {uuid: BuilderGuy, mob:COW, world: testWorld}. None of these queries conflict with
     * each other.
     * <br><br>Query A does not conflict with B, as the world columns do not have the same values. Query A does also
     * not conflict with C,
     * since the UUIDs don't match. Lastly, query B does not conflict with query C, since both the UUID and world
     * columns do not match.
     * <br>
     * <br>
     * A query X and query Y conflict when their values for each column match (the 'value' column is not checked as
     * it ought to be different for different columns).
     *
     * @param queries A list of queries to check whether they conflict with this query.
     * @return a list of queries (from the given queries list) that conflict with this query or an empty if no conflicts
     * were found.
     */
    public List<Query> findConflicts(List<Query> queries) {
        List<Query> conflictingQueries = new ArrayList<>();

        if (queries == null)
            return null;

        // Do reverse traversal
        for (int i = queries.size() - 1; i >= 0; i--) {

            // Get a query (compare.query) to compare this.query to.
            Query comparedQuery = queries.get(i);

            // Query is invalid.
            if (comparedQuery == null) {
                continue;
            }

            // Check to see if the given query conflicts with this.query.
            boolean isConflicting = this.conflicts(comparedQuery);

            // We have found a conflicting query, so add it to the list of conflicting queries.
            if (isConflicting) {
                conflictingQueries.add(comparedQuery);
            }
        }

        return conflictingQueries;

    }

    /**
     * Check whether a given query (compare.query) conflicts with this query.
     * <br>
     * <br>
     * A query X and query Y conflict when their values for each column match (the 'value' column is not checked as
     * it ought to be different for different columns).
     *
     * @param compareQuery Query to compare with this.query
     * @return true if it conflicts, false otherwise.
     */
    public boolean conflicts(Query compareQuery) {

        // Queries have to have a UUID column. If they don't, they can't conflict.
        if (!this.hasColumn("uuid") || !compareQuery.hasColumn("uuid")) {
            return false;
        }

        // If uuid of this.query is not the same as uuid of query.compare, they cannot conflict.
        if (!compareQuery.getUUID().equals(this.getUUID())) {
            return false;
        }

        // Queries can only confict when they have the same number of columns.
        // However, having the same size does not mean per se that they are conflicting.
        if (this.data.size() != compareQuery.data.size()) {
            return false;
        }

        // Loop over each (column, value) pair of this.query
        for (Entry<String, String> entry : data.entrySet()) {
            String columnName = entry.getKey();
            String columnValue = entry.getValue();

            // Ignore 'value' column, as it ought to be different.
            if (columnName.equalsIgnoreCase("value")) {
                continue;
            }

            // Get value of column of query.compare
            Object valueOfComparedQuery = compareQuery.getValue(columnName);

            // If query.compare does not have a value for this column, it cannot conflict with this.query
            if (valueOfComparedQuery == null) {
                return false;
            }

            // If value of condition in stored query is not the same as the given query, they cannot conflict.
            if (!valueOfComparedQuery.toString().equalsIgnoreCase(columnValue)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder("{");

        for (Entry<String, String> entry : data.entrySet()) {
            builder.append(entry.getKey() + ": " + entry.getValue() + ", ");
        }

        int lastComma = builder.lastIndexOf(",");

        if (lastComma >= 0) {
            builder.deleteCharAt(lastComma);
        }

        builder = new StringBuilder(builder.toString().trim());

        builder.append("}");

        return builder.toString();
    }

    /**
     * Add a value to the current value of the column (assuming it's a number).
     *
     * @param columnName Name of the column
     * @param value      Value to add to the current value of the column
     * @throws IllegalArgumentException if you are trying to add a value to a column that does not exist.
     * @throws NullPointerException     if given value is null
     */
    public void addValue(String columnName, Object value) throws IllegalArgumentException, NullPointerException {
        if (!this.hasColumn(columnName)) {
            throw new IllegalArgumentException(String.format("Column '%s' does not exist.", columnName));
        }

        if (value == null) {
            throw new NullPointerException("Value is null");
        }

        Double oldValue = Double.parseDouble(this.getValue("value").toString());

        Double updateValue = Double.parseDouble(value.toString());

        this.setValue(columnName, oldValue + updateValue);
    }

    /**
     * Gets the message for the log file.
     *
     * @return a message for the log file.
     */
    public String getLogString() {
        if (!this.hasColumn("value")) {
            return "Set playerName of " + this.getValue("uuid") + " to '" + this.getValue("playerName") + "'.";
        }

        return "Add value of " + this.getValue("uuid") + " with " + this.getValue() + " and query conditions " + data;
    }

    /**
     * Remove a column from the query
     *
     * @param columnName Name of the column to remove
     */
    public void removeColumn(String columnName) {
        data.remove(columnName.toLowerCase());
    }

    /**
     * Get the UUID that is associated with this Query.
     * <br>This will return null if there is no UUID found associated with this Query.
     *
     * @return uuid of the player for this Query or null if the UUID was not found.
     */
    public UUID getUUID() {
        if (!hasColumn("uuid")) {
            return null;
        }

        return UUID.fromString(this.getValue("uuid").toString());
    }

    /**
     * Get a copy of this query but remove some columns.
     * This can be used to get a copy of a query with only relevant information.
     *
     * @param columnName Names of the columns to be removed.
     * @return a copy of the query with the given column names removed.
     */
    public Query getFilteredCopy(String... columnName) {
        Query q = new Query(this.getData());

        for (String filteredColumn : columnName) {
            q.removeColumn(filteredColumn);
        }

        return q;
    }

    /**
     * Get the non-confliciting query of this.query and query.compare. This means that the value column is
     * set to the sum of the conflicting queries.
     *
     * @param compareQuery query to resolve conflicts with
     * @return query that has does not conflict with either of the two queries (this.query and query.compare).
     * @throws IllegalArgumentException if the given query does not conflict with this query or when there is a
     *                                  'value' column missing in one (or both) of the queries.
     */
    public Query resolveConflict(Query compareQuery) throws IllegalArgumentException {

        if (!this.conflicts(compareQuery)) {
            throw new IllegalArgumentException("Queries do not conflict!");
        }

        if (!compareQuery.hasColumn("value")) {
            throw new IllegalArgumentException(String.format("Query '%s' does not have a 'value' column",
                    compareQuery));
        }

        // Create new query that will be non-conflicting
        // Copy data from this query
        Query nonConflictingQuery = new Query(this.getData());

        // Add value of compareQuery to new query.
        nonConflictingQuery.addValue("value", compareQuery.getValue());

        return nonConflictingQuery;
    }

    /**
     * Get the non-conflicting query of this.query and a list of other queries.
     * See {@link #resolveConflict(Query)} for more info. If a query in the given list does not conflict, it will not
     * be used to create the resulting query.
     *
     * @param conflictingQueries List of queries that possibly conflict with this query.
     * @return a non-conflicting query that does not conflict with any of the queries.
     */
    public Query resolveConflicts(List<Query> conflictingQueries) {

        Query nonConflictingQuery = this;

        for (Query conflictingQuery : conflictingQueries) {
            if (nonConflictingQuery.conflicts(conflictingQuery)) {
                nonConflictingQuery = nonConflictingQuery.resolveConflict(conflictingQuery);
            }
        }

        return nonConflictingQuery;
    }

    /**
     * Check whether this query meets a given requirement. A query matches a requirement when it has the column
     * specified in the requirement and the value of that column specified in the requirement.
     *
     * @param requirement Requirement to validate
     * @return true if the requirement is met by this query, false otherwise.
     * @throws IllegalArgumentException if the requirement is null.
     */
    public boolean meetsRequirement(RowRequirement requirement) throws IllegalArgumentException {
        if (requirement == null) {
            throw new IllegalArgumentException("RowRequirement cannot be null.");
        }

        // This query does not have a column that is specified in the requirement, so it does not meet this requirement.
        if (!this.hasColumn(requirement.getColumnName())) {
            return false;
        }

        // This query does not have the same value as specified by the requirement, so it does not meet the requirement.
        return this.getValue(requirement.getColumnName()).equals(requirement.getColumnValue());
    }

    /**
     * Check whether this query meets all given requirements. See {@link #meetsRequirement(RowRequirement)} for more
     * information about meeting a requirement.
     *
     * @param requirements List of requirements that should be checked.
     * @return true if this query matches the list of given requirements.
     */
    public boolean meetsAllRequirements(Collection<RowRequirement> requirements) {
        for (RowRequirement requirement : requirements) {
            if (!this.meetsRequirement(requirement)) {
                return false;
            }
        }

        return true;
    }
}
