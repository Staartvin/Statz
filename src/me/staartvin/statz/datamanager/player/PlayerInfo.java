package me.staartvin.statz.datamanager.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Statistics about a player recorded by Statz
 * <p>
 * Date created: 15:07:07
 * 17 apr. 2016
 * 
 * @author "Staartvin"
 *
 */
public class PlayerInfo {

	private UUID uuid;
	private boolean isValid;

	private List<HashMap<String, Object>> results = new ArrayList<>();

	public PlayerInfo(final UUID uuid) {
		this.setUUID(uuid);
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(final UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Checks whether this player info is valid. Player info is considered valid
	 * when:
	 * <ul>
	 * <li>The data is not corrupt.
	 * <li>The query was valid and did not give any errors.
	 * <li>The requested data is stored about this player
	 * </ul>
	 * 
	 * @return true if valid info, false otherwise.
	 */
	public boolean isValid() {
		return isValid;
	}

	public void setValid(final boolean isValid) {
		this.isValid = isValid;
	}

	public String getValue(HashMap<String, String> map, final String key) {
		return map.get(key);
	}

	public List<HashMap<String, Object>> getResults() {
		return results;
	}

	public void setResults(List<HashMap<String, Object>> result) {
		this.results = result;
	}
	
	public void addResult(HashMap<String, Object> map) {
		this.results.add(map);
	}
}
