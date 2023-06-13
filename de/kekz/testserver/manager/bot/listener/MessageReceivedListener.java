package de.kekz.testserver.manager.bot.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.kekz.testserver.Main;
import de.kekz.testserver.manager.data.PlayerData;
import de.kekz.testserver.manager.verify.VerifySession;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReceivedListener extends ListenerAdapter {

	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		User user = event.getAuthor();

		if (user.isBot() || user.isFake()) {
			return;
		}

		PrivateChannel channel = event.getChannel();
		String message = event.getMessage().getContentRaw();
		String[] args = message.split(" ");

		if (message.startsWith("!verify")) {
			if (args.length == 2) {
				String name = args[1];
				Player target = Bukkit.getPlayer(name);

				if (target == null) {
					channel.sendMessage("The player was not found.").queue();
					return;
				}

				PlayerData data = Main.getInstance().getPlayerDataManager().getData(target);
				if (data.getVerifySession() != null) {
					channel.sendMessage("The player has an open verification session.").queue();
					return;
				}

				if (data.isVerified()) {
					channel.sendMessage("The player is already verified.").queue();
					return;
				}

				channel.sendMessage("Please check your ingame messages.").queue();

				VerifySession session = data.createVerifySession(Long.valueOf(user.getId()));
				data.sendMessage(Main.getInstance().getPrefix() + ChatColor.GRAY
						+ "Verification session was created. Please type " + ChatColor.GREEN + "/link "
						+ session.getVerificationId() + ChatColor.GRAY + " to confirm.");
				data.sendMessage(Main.getInstance().getPrefix() + ChatColor.GRAY + "Or you can cancel with "
						+ ChatColor.RED + "/link cancel.");
			} else {
				channel.sendMessage("Wrong syntax: !verify <ingame name>").queue();
			}
		} else {
			channel.sendMessage("Available commands: !verify <ingame name>").queue();
		}
	}
}
