package me.staartvin.statz.datamanager.player.specification;

import java.util.UUID;

public class VotesSpecification extends PlayerStatSpecification {

    public VotesSpecification(UUID uuid, int value) {
        super();

        this.putInData(Keys.UUID.toString(), uuid);
        this.putInData(Keys.VALUE.toString(), value);
    }

    public enum Keys {UUID, VALUE}
}
