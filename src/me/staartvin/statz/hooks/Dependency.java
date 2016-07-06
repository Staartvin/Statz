package me.staartvin.statz.hooks;

public enum Dependency {

	VOTIFIER("Votifier");
	
	Dependency(String internalName) {
		this.internalName = internalName;
	}

	private String internalName;
	
	public String getInternalString() {
		return this.internalName;
	}
}
