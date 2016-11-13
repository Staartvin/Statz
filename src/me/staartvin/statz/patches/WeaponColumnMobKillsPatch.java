package me.staartvin.statz.patches;

import java.util.Arrays;
import java.util.List;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;

public class WeaponColumnMobKillsPatch extends Patch {

	public WeaponColumnMobKillsPatch(Statz plugin) {
		super(plugin);
	}

	@Override
	public void applyChanges() {

		String tableName = this.getDatabaseConnector().getTable(PlayerStat.KILLS_MOBS).getTableName();

		List<String> queries = Arrays.asList(new String[] {
				"ALTER TABLE " + tableName + " ADD weapon VARCHAR(255) NOT NULL", "ALTER TABLE " + tableName
						+ " DROP INDEX `uuid`, ADD UNIQUE `uuid` (`uuid`, `mob`, `world`, `weapon`) USING BTREE;" });

		try {
			this.getDatabaseConnector().sendQueries(queries);

			this.getStatz().getConfigHandler().setLatestPatchVersion(this.getPatchId());
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}

	}

	@Override
	public String getPatchName() {
		return "Weapon Column - Mob Kills";
	}

	@Override
	public int getPatchId() {
		return 1;
	}

}
