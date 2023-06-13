package de.kekz.testserver.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.kekz.testserver.Main;
import de.kekz.testserver.manager.data.PlayerData;
import de.kekz.testserver.manager.ranks.Rank;
import net.dv8tion.jda.api.entities.User;

public class LoginListener implements Listener {

	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		Player p = e.getPlayer();

		/**
		 * Register
		 */
		Main.getInstance().getPlayerDataManager().register(p);
		Main.getInstance().getVerifyManager().register(p);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		PlayerData data = Main.getInstance().getPlayerDataManager().getData(p);

		data.refresh();
		data.loadCache();

		/* Did the user leave our discord server? */
		if (data.isVerified()) {
			User user = Main.getInstance().getBotManager().getBot().getUserById(data.getId());

			if (user == null) {
				data.setId(-1L);
				data.setVerified(false);
				data.setRank(Rank.USER);

				data.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED
						+ "It seems like you have left our Discord server. Your verification status has been removed!");
			}
		}

		Rank rank = data.getRank();
		e.setJoinMessage(
				ChatColor.GRAY + "[" + ChatColor.GREEN + "+" + ChatColor.GRAY + "] " + rank.getColor() + p.getName());

		data.updateScoreboard();
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		PlayerData data = Main.getInstance().getPlayerDataManager().getData(p);
		Rank rank = data.getRank();

		/**
		 * Unregister
		 */
		data.unregister();

		e.setQuitMessage(
				ChatColor.GRAY + "[" + ChatColor.RED + "-" + ChatColor.GRAY + "] " + rank.getColor() + p.getName());
	}
}
