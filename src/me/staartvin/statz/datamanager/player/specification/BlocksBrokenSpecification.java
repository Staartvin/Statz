package me.staartvin.statz.datamanager.player.specification;

import me.staartvin.statz.database.datatype.Query;
import org.bukkit.Material;

import java.util.Objects;
import java.util.UUID;

public class BlocksBrokenSpecification extends PlayerStatSpecification {

    public BlocksBrokenSpecification(UUID uuid, int value, String worldName, Material material) {
        super();

        this.putInData(Keys.UUID.toString(), uuid);
        this.putInData(Keys.VALUE.toString(), value);
        this.putInData(Keys.WORLD.toString(), worldName);
        this.putInData("block", material);
    }

    public static Material getBlockBroken(Query query) {
        Objects.requireNonNull(query);

        return (Material) query.getValue("block");
    }

    @Override
    public boolean hasWorldSupport() {
        return true;
    }

    public enum Keys {UUID, VALUE, WORLD, BLOCK}
}
