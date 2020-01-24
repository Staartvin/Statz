package me.staartvin.statz.datamanager.player.specification;

import me.staartvin.statz.database.datatype.Query;

import java.util.Objects;
import java.util.UUID;

public class WorldsChangedSpecification extends PlayerStatSpecification {

    public WorldsChangedSpecification(UUID uuid, int value, String worldName, String destinationWorld) {
        super();

        this.putInData(Keys.UUID.toString(), uuid);
        this.putInData(Keys.VALUE.toString(), value);
        this.putInData(Keys.WORLD.toString(), worldName);
        this.putInData("destWorld", destinationWorld);
    }

    public static String getDestinationWorld(Query query) {
        Objects.requireNonNull(query);

        return query.getValue("destWorld").toString();
    }

    @Override
    public boolean hasWorldSupport() {
        return true;
    }

    public enum Keys {UUID, VALUE, WORLD, DESTINATION_WORLD}
}
