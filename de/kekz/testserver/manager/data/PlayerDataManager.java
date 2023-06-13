package de.kekz.testserver.manager.data;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerDataManager {

	// could improve this with a HashMap
	private ArrayList<PlayerData> datas = new ArrayList<PlayerData>();

	public PlayerDataManager() {
		load();
	}

	public void load() {
		try {
			/* Clear */
			clear();

			/* register */
			Bukkit.getOnlinePlayers().stream().filter(p -> p != null).forEach(p -> register(p));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		try {
			/* Loading online data */
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unload() {
		try {

			/* load */
			getDatas().stream().filter(data -> data != null).forEach(data -> data.uploadCache());

			/* Clear */
			clear();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clear() {
		if (getDatas().size() <= 0) {
			return;
		}

		try {
			/* Unload existing datas */
			Bukkit.getOnlinePlayers().stream().filter(p -> p != null).filter(p -> isRegistered(p))
					.forEach(p -> unregister(p));

			/* Clearing the datas list */
			getDatas().clear();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void register(Player player) {
		try {

			if (isRegistered(player)) {
				unregister(player);
			}

			PlayerData data = new PlayerData(player);
			getDatas().add(data);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unregister(Player player) {
		try {
			PlayerData data = getData(player);

			if (data != null) {
				getDatas().remove(data);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isRegistered(Player player) {
		return getDatas().contains(getData(player.getName()));
	}

	public PlayerData getData(Player player) {
		for (PlayerData data : getDatas()) {
			if (data.getUniqueId().equals(player.getUniqueId())) {
				return data;
			}
		}
		return null;
	}

	public PlayerData getData(UUID uniqueId) {
		for (PlayerData data : getDatas()) {
			if (data.getUniqueId().equals(uniqueId)) {
				return data;
			}
		}
		return null;
	}

	public PlayerData getData(String name) {
		for (PlayerData data : getDatas()) {
			if (data.getName().equals(name)) {
				return data;
			}
		}
		return null;
	}

	public ArrayList<PlayerData> getDatas() {
		return datas;
	}
}
