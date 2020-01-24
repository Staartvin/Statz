package me.staartvin.statz.datamanager.player.specification;

import me.staartvin.statz.database.datatype.Query;

import java.util.Objects;
import java.util.UUID;

public class KillsMobsSpecification extends PlayerStatSpecification {

    public KillsMobsSpecification(UUID uuid, int value, String worldName, String mob, String weapon) {
        super();

        this.putInData(Keys.UUID.toString(), uuid);
        this.putInData(Keys.VALUE.toString(), value);
        this.putInData(Keys.WORLD.toString(), worldName);
        this.putInData("mob", mob);
        this.putInData("weapon", weapon);
    }

    public static String getMobKilled(Query query) {
        Objects.requireNonNull(query);

        return query.getValue("mob").toString();
    }

    public static String getWeapon(Query query) {
        Objects.requireNonNull(query);

        return query.getValue("weapon").toString();
    }

    @Override
    public boolean hasWorldSupport() {
        return true;
    }

    public enum Keys {UUID, VALUE, WORLD, MOB, WEAPON}
}
