package de.kekz.testserver.listener;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import de.kekz.testserver.Main;
import de.kekz.testserver.manager.data.PlayerData;
import de.kekz.testserver.manager.ranks.Rank;

public class WorldListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		PlayerData playerData = Main.getInstance().getPlayerDataManager().getData(p.getUniqueId());

		if (!playerData.isVerified()) {
			e.setCancelled(true);
			playerData.sendMessage(
					Main.getInstance().getPrefix() + ChatColor.RED + "You need to be verified to write chat messages!");
			playerData.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED
					+ "Join our Discord server and send a private message to the bot: discord.gg/xhxjvYq");
		} else {
			Rank rank = playerData.getRank();
			e.setFormat(
					rank.getColor() + playerData.getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + e.getMessage());
		}
	}

	@EventHandler
	public void onWorld(WeatherChangeEvent e) {
		World world = e.getWorld();

		// hard-coded because Map isn't changing for a long time
		if (world != null && world.getName().equals("Testworld")) {
			if (world.getDifficulty() != Difficulty.PEACEFUL) {
				world.setDifficulty(Difficulty.PEACEFUL);
			}
			if (world.getGameRuleValue("doMobSpawning").equals("true")) {
				world.setGameRuleValue("doMobSpawning", "false");
			}
			if (world.getGameRuleValue("doDaylightCycle").equals("true")) {
				world.setGameRuleValue("doDaylightCycle", "false");
			}
			if (world.isThundering()) {
				world.setThundering(false);
			}
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onAchievement(PlayerAchievementAwardedEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		e.blockList().clear();
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		Entity d = e.getDamager(), en = e.getEntity();
		if (d != null && en != null && en instanceof Player) {
			e.setDamage(0D);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		PlayerData playerData = Main.getInstance().getPlayerDataManager().getData(p.getUniqueId());

		if (playerData.getPlayer().getGameMode() == GameMode.CREATIVE) {
			e.setBuild(true);
			e.setCancelled(false);
			return;
		}

		Block block = e.getBlock();
		Location bLoc = block.getLocation();

		// again hard-coded...
		if (!bLoc.getWorld().getName().equalsIgnoreCase("Testworld")) {
			e.setBuild(false);
			e.setCancelled(true);
			return;
		}

		double x = bLoc.getX(), z = bLoc.getZ();
		if (x >= 15D && x <= 31D && z >= 15D && z <= 31D) {
			e.setBuild(true);
			e.setCancelled(false);
		} else {
			e.setBuild(false);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		PlayerData playerData = Main.getInstance().getPlayerDataManager().getData(p.getUniqueId());

		if (playerData.getPlayer().getGameMode() == GameMode.CREATIVE) {
			e.setCancelled(false);
			return;
		}

		Block block = e.getBlock();
		Location bLoc = block.getLocation();

		// again hard-coded...
		if (!bLoc.getWorld().getName().equalsIgnoreCase("Testworld")) {
			e.setCancelled(true);
			return;
		}

		double x = bLoc.getX(), y = bLoc.getY(), z = bLoc.getZ();
		if (x >= 15D && x <= 31D && z >= 15D && z <= 31D && y >= 4D && y <= 116D) {
			e.setCancelled(false);
		} else {
			e.setCancelled(true);
		}
	}
}
