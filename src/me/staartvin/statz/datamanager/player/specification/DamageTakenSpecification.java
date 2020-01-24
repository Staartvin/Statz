package me.staartvin.statz.datamanager.player.specification;

import me.staartvin.statz.database.datatype.Query;
import org.bukkit.Material;

import java.util.Objects;
import java.util.UUID;

public class DamageTakenSpecification extends PlayerStatSpecification {

    public DamageTakenSpecification(UUID uuid, double value, String worldName, String cause) {
        super();

        this.putInData(Keys.UUID.toString(), uuid);
        this.putInData(Keys.VALUE.toString(), value);
        this.putInData(Keys.WORLD.toString(), worldName);
        this.putInData("cause", cause);
    }

    public static Material getCauseBroken(Query query) {
        Objects.requireNonNull(query);

        return (Material) query.getValue("cause");
    }

    @Override
    public boolean hasWorldSupport() {
        return true;
    }

    public enum Keys {UUID, VALUE, WORLD, CAUSE}
}
