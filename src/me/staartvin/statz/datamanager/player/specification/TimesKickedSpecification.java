package me.staartvin.statz.datamanager.player.specification;

import me.staartvin.statz.database.datatype.Query;
import org.bukkit.Material;

import java.util.Objects;
import java.util.UUID;

public class TimesKickedSpecification extends PlayerStatSpecification {

    public TimesKickedSpecification(UUID uuid, int value, String worldName, String reason) {
        super();

        this.putInData(Keys.UUID.toString(), uuid);
        this.putInData(Keys.VALUE.toString(), value);
        this.putInData(Keys.WORLD.toString(), worldName);
        this.putInData("reason", reason);
    }

    public static Material getReason(Query query) {
        Objects.requireNonNull(query);

        return (Material) query.getValue("reason");
    }

    @Override
    public boolean hasWorldSupport() {
        return true;
    }

    public enum Keys {UUID, VALUE, WORLD, REASON}
}
