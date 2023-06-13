package de.kekz.testserver.commands.rank;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.kekz.testserver.Main;
import de.kekz.testserver.manager.data.PlayerData;
import de.kekz.testserver.manager.data.enums.DataInformation;
import de.kekz.testserver.manager.ranks.Rank;
import net.dv8tion.jda.api.entities.User;

public class SetRankCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(
					Main.getInstance().getPrefix() + ChatColor.RED + "Only players can execute this command.");
			return false;
		}

		Player p = (Player) sender;
		PlayerData data = Main.getInstance().getPlayerDataManager().getData(p);

		if (data.getPlayer().isOp() ? false : data.getRank().getPermission() < Rank.AUTHOR.getPermission()) {
			data.sendNoPermission();
			return false;
		}

		if (args.length == 2) {
			String target = args[0], input = args[1], uuid = null;
			long id = -1;

			if (!Main.getInstance().getVerifyManager().isRegistered(DataInformation.NAME, target)) {
				data.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED + "The player was not found.");
				return false;
			}

			Rank rank = Rank.getRankByName(input);
			if (rank == null) {
				data.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED + "The rank was not found.");
				return false;
			}

			uuid = Main.getInstance().getVerifyManager().getUniqueId(DataInformation.NAME, target);
			target = Main.getInstance().getVerifyManager().getName(DataInformation.UUID, uuid);
			id = Main.getInstance().getVerifyManager().getDiscordId(DataInformation.UUID, uuid);

			User user = Main.getInstance().getBotManager().getBot().getUserById(id);
			if (user == null) {
				data.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED
						+ "It looks like the player is not on our Discord server.");
				return false;
			}

			if (!Main.getInstance().getVerifyManager().getVerificationStatus(DataInformation.UUID, uuid)) {
				data.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED + "The player is not verified.");
				return false;
			}

			Rank oldRank = Main.getInstance().getVerifyManager().getRank(DataInformation.UUID, uuid);
			if (rank.equals(oldRank)) {
				data.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED + "The player already has this rank.");
				return false;
			}

			Player t = Bukkit.getPlayer(target);
			if (t != null) {
				PlayerData tData = Main.getInstance().getPlayerDataManager().getData(p);

				tData.setRank(rank);
				tData.sendMessage(Main.getInstance().getPrefix() + "Your rank was updated to: " + rank.getColor()
						+ rank.getName());
				tData.getPlayer().playSound(tData.getLocation(), Sound.LEVEL_UP, 1, 1);

				Main.getInstance().getScoreBoardManager().updateScoreboard(tData, true);
			}

			Main.getInstance().getRankManager().setRank(user, rank, DataInformation.UUID, uuid);
			data.sendMessage(Main.getInstance().getPrefix() + "Updated rank from player " + ChatColor.YELLOW + target
					+ ChatColor.GRAY + " to: " + rank.getColor() + rank.getName());
		} else {
			data.sendMessage(Main.getInstance().getPrefix() + "/setrank <Player> <Rank>");
		}
		return false;
	}
}
