package de.kekz.testserver.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.kekz.testserver.Main;
import de.kekz.testserver.manager.data.PlayerData;
import de.kekz.testserver.manager.verify.VerifySession;

public class LinkCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(
					Main.getInstance().getPrefix() + ChatColor.RED + "Only players can execute this command.");
			return false;
		}

		Player p = (Player) sender;
		PlayerData data = Main.getInstance().getPlayerDataManager().getData(p);
		VerifySession verifySession = data.getVerifySession();

		if (data.isVerified()) {
			data.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED + "You are already verified.");
			return false;
		}

		if (verifySession == null) {
			data.sendMessage(
					Main.getInstance().getPrefix() + ChatColor.RED + "You don't have any open verification session.");
			return false;
		}

		if (args.length == 1) {
			String input = args[0];
			if (input.equalsIgnoreCase("cancel")) {
				data.cancelVerifySession();
				data.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED
						+ "Your verification session has been cancelled.");
				return false;
			}

			if (!input.equals(verifySession.getVerificationId())) {
				data.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED + "Invalid verification id.");
				return false;
			}

			data.acceptVerifySession();
			data.sendMessage(
					Main.getInstance().getPrefix() + ChatColor.GREEN + "Your account has been verified successfully.");
		} else {
			data.sendMessage(Main.getInstance().getPrefix() + "/link <key | cancel>");
		}

		return false;
	}
}
