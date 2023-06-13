package de.kekz.testserver.manager.bot;

import javax.security.auth.login.LoginException;

import de.kekz.testserver.Main;
import de.kekz.testserver.manager.bot.listener.MemberJoinListener;
import de.kekz.testserver.manager.bot.listener.MessageReceivedListener;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

public class BotManager {

	private JDA bot;

	public void create() {
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		builder.setToken("PRIVATE TOKEN");

		/* Status */
		builder.setStatus(OnlineStatus.ONLINE);
		builder.setActivity(Activity.watching("Paradox Discord- and Testserver"));

		/* Listener */
		builder.addEventListeners(new MessageReceivedListener());
		builder.addEventListeners(new MemberJoinListener());

		try {
			this.bot = builder.build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		this.bot.shutdown();
	}

	public Guild getGuild() {
		return Main.getInstance().getBotManager().getBot().getGuilds().get(0);
	}

	public JDA getBot() {
		return bot;
	}
}
