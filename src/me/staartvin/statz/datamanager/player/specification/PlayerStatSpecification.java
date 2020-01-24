package me.staartvin.statz.datamanager.player.specification;

import me.staartvin.statz.database.datatype.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class PlayerStatSpecification {

    private Map<String, Object> data = new HashMap<>();

    public static UUID getUUID(Query query) {
        Objects.requireNonNull(query);

        return query.getUUID();
    }

    public static double getValue(Query query) {
        Objects.requireNonNull(query);

        return query.getValue();
    }

    public static String getWorldName(Query query) {
        Objects.requireNonNull(query);

        return query.getValue("world").toString();
    }

    public Query constructQuery() {
        Query query = new Query(data);
        query.setSpecification(this.getClass());
        return query;
    }

    protected void putInData(String key, Object value) {
        this.data.put(key, value);
    }

    public boolean hasWorldSupport() {
        return false;
    }

}
