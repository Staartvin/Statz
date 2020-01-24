package me.staartvin.statz.datamanager.player.specification;

import me.staartvin.statz.database.datatype.Query;
import org.bukkit.Material;

import java.util.Objects;
import java.util.UUID;

public class ItemsPickedUpSpecification extends PlayerStatSpecification {

    public ItemsPickedUpSpecification(UUID uuid, int value, String worldName, Material material) {
        super();

        this.putInData(Keys.UUID.toString(), uuid);
        this.putInData(Keys.VALUE.toString(), value);
        this.putInData(Keys.WORLD.toString(), worldName);
        this.putInData("item", material);
    }

    public static Material getItemPickedUp(Query query) {
        Objects.requireNonNull(query);

        return (Material) query.getValue("item");
    }

    @Override
    public boolean hasWorldSupport() {
        return true;
    }

    public enum Keys {UUID, VALUE, WORLD, ITEM}
}
