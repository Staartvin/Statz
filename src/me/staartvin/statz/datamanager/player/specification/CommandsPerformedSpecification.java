package me.staartvin.statz.datamanager.player.specification;

import me.staartvin.statz.database.datatype.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CommandsPerformedSpecification extends PlayerStatSpecification {

    public CommandsPerformedSpecification(UUID uuid, int value, String worldName, String command, String arguments) {
        super();

        this.putInData(Keys.UUID.toString(), uuid);
        this.putInData(Keys.VALUE.toString(), value);
        this.putInData(Keys.WORLD.toString(), worldName);
        this.putInData("command", command);
        this.putInData("arguments", arguments);
    }

    public static String getCommand(Query query) {
        Objects.requireNonNull(query);

        return query.getValue("command").toString();
    }

    public static List<String> getArguments(Query query) {
        Objects.requireNonNull(query);

        String argumentsString = query.getValue("arguments").toString();

        return Arrays.asList(argumentsString.split(" "));
    }

    @Override
    public boolean hasWorldSupport() {
        return true;
    }

    public enum Keys {UUID, VALUE, WORLD, COMMAND, ARGUMENTS}
}
