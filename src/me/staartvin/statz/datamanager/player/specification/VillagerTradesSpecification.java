package me.staartvin.statz.datamanager.player.specification;

import org.bukkit.Material;

import java.util.UUID;

public class VillagerTradesSpecification extends PlayerStatSpecification {

    public VillagerTradesSpecification(UUID uuid, int value, String worldName, Material traded) {
        super();

        this.putInData(Keys.UUID.toString(), uuid);
        this.putInData(Keys.VALUE.toString(), value);
        this.putInData(Keys.WORLD.toString(), worldName);
        this.putInData(Keys.TRADE.toString(), traded.toString());
    }

    @Override
    public boolean hasWorldSupport() {
        return true;
    }

    public enum Keys {UUID, VALUE, WORLD, TRADE}
}
