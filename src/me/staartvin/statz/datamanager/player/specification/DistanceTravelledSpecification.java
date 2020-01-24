package me.staartvin.statz.datamanager.player.specification;

import me.staartvin.statz.database.datatype.Query;

import java.util.Objects;
import java.util.UUID;

public class DistanceTravelledSpecification extends PlayerStatSpecification {

    public DistanceTravelledSpecification(UUID uuid, double value, String worldName, String movementType) {
        super();

        this.putInData(Keys.UUID.toString(), uuid);
        this.putInData(Keys.VALUE.toString(), value);
        this.putInData(Keys.WORLD.toString(), worldName);
        this.putInData("moveType", movementType);
    }

    public static String getMovementType(Query query) {
        Objects.requireNonNull(query);

        return query.getValue("moveType").toString();
    }

    @Override
    public boolean hasWorldSupport() {
        return true;
    }

    public enum Keys {UUID, VALUE, WORLD, MOVE_TYPE}
}
