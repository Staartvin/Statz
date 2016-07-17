package me.staartvin.statz.listeners;

import org.bukkit.entity.Chicken;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import me.staartvin.statz.Statz;
import me.staartvin.statz.database.datatype.Query;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

public class EntityDeathListener implements Listener {

	private final Statz plugin;

	public EntityDeathListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDie(final EntityDeathEvent event) {

		PlayerStat stat = PlayerStat.KILLS_MOBS;

		Entity e = event.getEntity();

		if (!(e.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
			return;
		}

		EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent) e.getLastDamageCause();
		if (nEvent.getDamager() instanceof Player) {
			// Entity died because of Player
			// Killer
			Player player = (Player) nEvent.getDamager();
			
			if (e instanceof Player) {
				// Player killed player
				
				stat = PlayerStat.KILLS_PLAYERS;
				
				Player murderedPlayer = (Player) e;
				
				//Get player info.
				final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat);

				// Get current value of stat.
				int currentValue = 0;

				// Check if it is valid!
				if (info.isValid()) {
					for (Query map : info.getResults()) {
						if (map.getValue("world") != null
								&& map.getValue("world").toString().equalsIgnoreCase(player.getWorld().getName())
								&& map.getValue("playerKilled") != null
										&& map.getValue("playerKilled").toString().equalsIgnoreCase(murderedPlayer.getName())) {
							currentValue += Integer.parseInt(map.getValue("value").toString());
						}
					}
					//currentValue = Integer.parseInt(info.getResults().getValue(0).getValue("value").toString());
				}

				// Update value to new stat.
				plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
						StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", (currentValue + 1),
								"world", player.getWorld().getName(), "playerKilled", murderedPlayer.getName()));
				
			} else {
				// Player killed mob		
				
				String mobType = e.getName().toUpperCase();
				
				if (e instanceof Skeleton) {
					final Skeleton ske = (Skeleton) e;

					if (ske.getSkeletonType() == SkeletonType.WITHER) {
						mobType = "WITHER " + mobType;
					}
				} else if (e instanceof Creeper) {
					final Creeper cre = (Creeper) e;

					if (cre.isPowered()) {
						mobType = "POWERED " + mobType;
					}
				} else if (e instanceof Chicken) {
					final Chicken mob = (Chicken) e;

					if (mob.getPassenger() != null) {
						mobType = mobType + " JOCKEY";
					}
				} else if (e instanceof Rabbit) {
					final Rabbit mob = (Rabbit) e;

					if (mob.getRabbitType() == Type.THE_KILLER_BUNNY) {
						//mobType = "KILLER " + mobType;
					}
				} else if (e instanceof Spider) {
					final Spider mob = (Spider) e;

					if (mob.getPassenger() != null) {
						mobType = mobType + " JOCKEY";
					}
				} else if (e instanceof Guardian) {
					final Guardian mob = (Guardian) e;

					if (mob.isElder()) {
						mobType = "ELDER " + mobType;
					}
				}

				//Get player info.
				final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat);

				// Get current value of stat.
				int currentValue = 0;

				// Check if it is valid!
				if (info.isValid()) {
					for (Query map : info.getResults()) {
						if (map.getValue("world") != null
								&& map.getValue("world").toString().equalsIgnoreCase(player.getWorld().getName())
								&& map.getValue("mob") != null
										&& map.getValue("mob").toString().equalsIgnoreCase(mobType)) {
							currentValue += Double.parseDouble(map.getValue("value").toString());
						}
					}
					//currentValue = Integer.parseInt(info.getResults().getValue(0).getValue("value").toString());
				}

				// Update value to new stat.
				plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
						StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", (currentValue + 1),
								"world", player.getWorld().getName(), "mob", mobType));

			}
		} else {
			// Entity died of something else
		}

		//		
	}
}
