package de.kekz.testserver.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.kekz.testserver.Main;
import de.kekz.testserver.manager.data.PlayerData;

public class UnlinkCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(
					Main.getInstance().getPrefix() + ChatColor.RED + "Only players can execute this command.");
			return false;
		}

		Player p = (Player) sender;
		PlayerData data = Main.getInstance().getPlayerDataManager().getData(p);

		if (!data.isVerified()) {
			data.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED + "You are not verified.");
			return false;
		}

		if (args.length == 0) {
			data.removeVerifyStatus();
			data.sendMessage(Main.getInstance().getPrefix() + ChatColor.RED + "Your verification status was removed.");
		} else {
			data.sendMessage(Main.getInstance().getPrefix() + "/unlink");
		}

		return false;
	}
}
