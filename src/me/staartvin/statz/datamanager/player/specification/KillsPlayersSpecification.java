package me.staartvin.statz.datamanager.player.specification;

import me.staartvin.statz.database.datatype.Query;

import java.util.Objects;
import java.util.UUID;

public class KillsPlayersSpecification extends PlayerStatSpecification {

    public KillsPlayersSpecification(UUID uuid, int value, String worldName, String playerKilled) {
        super();

        this.putInData(Keys.UUID.toString(), uuid);
        this.putInData(Keys.VALUE.toString(), value);
        this.putInData(Keys.WORLD.toString(), worldName);
        this.putInData("playerKilled", playerKilled);
    }

    public static String getPlayerKilled(Query query) {
        Objects.requireNonNull(query);

        return query.getValue("playerKilled").toString();
    }

    @Override
    public boolean hasWorldSupport() {
        return true;
    }

    public enum Keys {UUID, VALUE, WORLD, PLAYER}
}
