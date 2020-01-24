package me.staartvin.statz.datamanager.player.specification;

import me.staartvin.statz.database.datatype.Query;

import java.util.Objects;
import java.util.UUID;

public class TeleportsSpecification extends PlayerStatSpecification {

    public TeleportsSpecification(UUID uuid, int value, String worldName, String destinationWorld, String cause) {
        super();

        this.putInData(Keys.UUID.toString(), uuid);
        this.putInData(Keys.VALUE.toString(), value);
        this.putInData(Keys.WORLD.toString(), worldName);
        this.putInData("destWorld", destinationWorld);
        this.putInData("cause", cause);
    }

    public static String getDestinationWorld(Query query) {
        Objects.requireNonNull(query);

        return query.getValue("destWorld").toString();
    }

    public static String getCause(Query query) {
        Objects.requireNonNull(query);

        return query.getValue("cause").toString();
    }

    @Override
    public boolean hasWorldSupport() {
        return true;
    }

    public enum Keys {UUID, VALUE, WORLD, DESTINATION_WORLD, CAUSE}
}
