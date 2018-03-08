package me.staartvin.statz.tasks;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;

import java.util.UUID;


public class UpdatePlayerCacheTask implements Runnable {

    private UUID uuid;
    private Statz plugin;

    public UpdatePlayerCacheTask(Statz instance, UUID uuid) {
        this.uuid = uuid;
        this.plugin = instance;
    }

    @Override
    public void run() {

        System.out.println("-----------------");
        for (PlayerStat statType : PlayerStat.values()) {
            long startTime = System.currentTimeMillis();

            System.out.println(String.format("Update cache of %s for stat %s!", uuid, statType));
            PlayerInfo info = plugin.getDataManager().getPlayerInfo(uuid, statType);

            System.out.println("Got info: " + info);
            System.out.println(String.format("Took %d ms.", System.currentTimeMillis() - startTime));
        }


    }
}
