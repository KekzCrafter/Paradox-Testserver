package de.kekz.testserver.manager.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import de.kekz.testserver.Main;
import de.kekz.testserver.manager.data.PlayerData;
import de.kekz.testserver.manager.ranks.Rank;

public class ScoreBoardManager {

	private char[] alphabet = "abcdefghijklmnopqrstuvwxybcdefghijklmnopqrstuvwxyz".toCharArray();

	private Scoreboard sendScoreboard(PlayerData data, Scoreboard scoreboard) {
		Objective obj = scoreboard.getObjective("aaa") != null ? scoreboard.getObjective("aaa")
				: scoreboard.registerNewObjective("aaa", "bbb");
		obj.setDisplayName(ChatColor.BLUE + "Paradox Testserver");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		Rank rank = data.getRank();

		obj.getScore("   ").setScore(6);
		obj.getScore(ChatColor.WHITE + "Rank" + ChatColor.GRAY + ":").setScore(5);
		obj.getScore(ChatColor.GRAY + "• " + rank.getColor() + rank.getName()).setScore(4);
		obj.getScore("  ").setScore(3);
		obj.getScore(ChatColor.WHITE + "Discord" + ChatColor.GRAY + ":").setScore(2);
		obj.getScore(ChatColor.GRAY + "• " + ChatColor.AQUA + "discord.gg/xhxjvYq").setScore(1);
		obj.getScore(" ").setScore(0);
		return scoreboard;
	}

	@SuppressWarnings("deprecation")
	public void updateScoreboard(PlayerData data, boolean updateOther) {
		ScoreboardManager sm = Bukkit.getScoreboardManager();
		Scoreboard s = sm.getNewScoreboard();

		for (Player players : Bukkit.getOnlinePlayers()) {
			PlayerData allData = Main.getInstance().getPlayerDataManager().getData(players);
			String t = getSortedTeam(allData);

			if (s.getTeam(t) != null) {
				Team team = s.getTeam(t);
				team.addPlayer(players);
			} else {
				Team team = s.registerNewTeam(t);
				players.setPlayerListName(getPrefix(allData) + players.getName());
				team.setDisplayName(getTeam(allData));
				team.setNameTagVisibility(NameTagVisibility.ALWAYS);
				team.setPrefix(getPrefix(allData));
				team.addPlayer(players);
			}
		}
		sendScoreboard(data, s);
		data.getPlayer().setScoreboard(s);

		if (updateOther) {
			for (Player players : Bukkit.getOnlinePlayers()) {
				PlayerData allData = Main.getInstance().getPlayerDataManager().getData(players);
				if (players.getUniqueId().equals(data.getUniqueId())) {
					break;
				}
				updateScoreboard(allData, false);
			}
		}
	}

	private String getPrefix(PlayerData data) {
		Rank rank = data.getRank();
		return rank.getColor()
				+ (rank == Rank.AUTHOR ? rank.getName() + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY : "");
	}

	private String getTeam(PlayerData data) {
		return data.getRank().getName();
	}

	private String getSortedTeam(PlayerData data) {
		int id = 0;
		String team;
		Rank rank = data.getRank();

		team = rank.getName();
		id = rank.getId();

		return alphabet[id] + team;
	}
}
