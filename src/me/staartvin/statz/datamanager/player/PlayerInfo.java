package me.staartvin.statz.datamanager.player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Statistics about a player recorded by Statz
 * <p>
 * Date created:  15:07:07
 * 17 apr. 2016
 * @author "Staartvin"
 *
 */
public class PlayerInfo {
	
	private UUID uuid;
	private boolean isValid;
	
	private HashMap<String, String> results = new HashMap<String, String>();
	
	public PlayerInfo(UUID uuid) {
		this.setUUID(uuid);
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Checks whether this player info is valid. Player info is considered valid when:
	 * <ul>
	 * <li>The data is not corrupt.
	 * <li>The query was valid and did not give any errors.
	 * <li>The requested data is stored about this player
	 * </ul>
	 * @return true if valid info, false otherwise.
	 */
	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	
	public String getValue(String key) {
		return results.get(key);
	}
	
	public void setValue(String key, String value) {
		this.results.put(key, value);
	}
	
}
