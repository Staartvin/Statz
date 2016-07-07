package me.staartvin.statz.hooks;

public enum Dependency {

	VOTIFIER("Votifier"), JOBS("Jobs"), MCMMO("mcMMO"), ASKYBLOCK("ASkyBlock"), ACIDISLAND("AcidIsland"), WORLDGUARD(
			"WorldGuard"), ROYAL_COMMANDS("RoyalCommands"), ON_TIME("OnTime"), AFKTERMINATOR(
					"afkTerminator"), ESSENTIALS("Essentials"), FACTIONS("Factions"), STATISTICS(
							"Statistics"), STATS("Stats"), ULTIMATE_CORE("UltimateCore"), VAULT("Vault");

	Dependency(String internalName) {
		this.internalName = internalName;
	}

	private String internalName;

	public String getInternalString() {
		return this.internalName;
	}
}
