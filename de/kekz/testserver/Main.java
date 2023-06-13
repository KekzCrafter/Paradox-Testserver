package de.kekz.testserver;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import de.kekz.testserver.commands.LinkCommand;
import de.kekz.testserver.commands.SpawnCommand;
import de.kekz.testserver.commands.UnlinkCommand;
import de.kekz.testserver.commands.rank.SetRankCommand;
import de.kekz.testserver.listener.LoginListener;
import de.kekz.testserver.listener.WorldListener;
import de.kekz.testserver.manager.MySQL;
import de.kekz.testserver.manager.bot.BotManager;
import de.kekz.testserver.manager.crypto.CryptoManager;
import de.kekz.testserver.manager.data.PlayerDataManager;
import de.kekz.testserver.manager.ranks.RankManager;
import de.kekz.testserver.manager.scoreboard.ScoreBoardManager;
import de.kekz.testserver.manager.verify.VerifyManager;

public class Main extends JavaPlugin {

	/* Main instance */
	private static Main instance;

	/* Manager */
	private ScoreBoardManager scoreBoardManager;
	private PlayerDataManager playerDataManager;
	private CryptoManager cryptoManager;
	private VerifyManager verifyManager;
	private RankManager rankManager;
	private BotManager botManager;

	/* MySQL */
	private MySQL mySQL;

	@Override
	public void onEnable() {
		enable();
	}

	@Override
	public void onDisable() {
		disable();
	}

	private void enable() {

		/* instance */
		instance = this;

		/* manager */
		this.scoreBoardManager = new ScoreBoardManager();
		this.playerDataManager = new PlayerDataManager();
		this.cryptoManager = new CryptoManager();
		this.verifyManager = new VerifyManager();
		this.rankManager = new RankManager();
		this.botManager = new BotManager();

		/* config */
		saveDefaultConfig();

		/* mysql */
		connectMySQL();

		/* commands */
		registerCommands();

		/* listener */
		registerListener();

		/* bot */
		getBotManager().create();

	}

	private void disable() {

		/* listener */
		unregisterListener();

		/* bot */
		getBotManager().shutdown();

		/* data */
		if (getPlayerDataManager() != null) {
			getPlayerDataManager().unload();
		}

		/* mysql */
		disconnectMySQL();

	}

	private void registerCommands() {
		try {
			getCommand("link").setExecutor(new LinkCommand());
			getCommand("unlink").setExecutor(new UnlinkCommand());
			getCommand("setrank").setExecutor(new SetRankCommand());
			getCommand("spawn").setExecutor(new SpawnCommand());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void registerListener() {
		try {
			Bukkit.getServer().getPluginManager().registerEvents(new LoginListener(), this);
			Bukkit.getServer().getPluginManager().registerEvents(new WorldListener(), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unregisterListener() {
		try {
			HandlerList.unregisterAll(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void connectMySQL() {
		try {
			this.mySQL = new MySQL();

			String username = getDefaultConfig().getString("Username"),
					password = getDefaultConfig().getString("Password"),
					database = getDefaultConfig().getString("Database"), host = getDefaultConfig().getString("Host");

			// this is just temporary
			if (username.startsWith("=")) {
				username = getCryptoManager().decrypt(username.substring(1));
			} else {
				if (!username.equalsIgnoreCase("None")) {
					getDefaultConfig().set("Username", "=" + getCryptoManager().encrypt(username));
				}
			}

			if (password.startsWith("=")) {
				password = getCryptoManager().decrypt(password.substring(1));
			} else {
				if (!password.equalsIgnoreCase("None")) {
					getDefaultConfig().set("Password", "=" + getCryptoManager().encrypt(password));
				}
			}

			if (database.startsWith("=")) {
				database = getCryptoManager().decrypt(database.substring(1));
			} else {
				if (!database.equalsIgnoreCase("None")) {
					getDefaultConfig().set("Database", "=" + getCryptoManager().encrypt(database));
				}
			}

			if (host.startsWith("=")) {
				host = getCryptoManager().decrypt(host.substring(1));
			} else {
				if (!host.equalsIgnoreCase("None")) {
					getDefaultConfig().set("Host", "=" + getCryptoManager().encrypt(host));
				}
			}

			this.mySQL.username = username;
			this.mySQL.password = password;
			this.mySQL.database = database;
			this.mySQL.host = host;
			this.mySQL.port = "3306";

			if (this.mySQL.validValues()) {
				this.mySQL.createConnection();
				getVerifyManager().createTable();
			} else {
				log(ChatColor.RED + "MySQL values are not valid (check the config.yml)...");
			}

			saveConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void disconnectMySQL() {
		try {
			getMySQL().closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Instance
	 */
	public static Main getInstance() {
		return instance;
	}

	/**
	 * MySQL
	 */
	public MySQL getMySQL() {
		return mySQL;
	}

	/**
	 * Config
	 */
	public FileConfiguration getDefaultConfig() {
		return getConfig();
	}

	/**
	 * Prefix
	 */
	public String getPrefix() {
		return ChatColor.GRAY + "[" + ChatColor.YELLOW + "Testserver" + ChatColor.GRAY + "] ";
	}

	/**
	 * Manager
	 */
	public ScoreBoardManager getScoreBoardManager() {
		return scoreBoardManager;
	}

	public PlayerDataManager getPlayerDataManager() {
		return playerDataManager;
	}

	public CryptoManager getCryptoManager() {
		return cryptoManager;
	}

	public VerifyManager getVerifyManager() {
		return verifyManager;
	}

	public RankManager getRankManager() {
		return rankManager;
	}

	public BotManager getBotManager() {
		return botManager;
	}

	public void log(String message) {
		Bukkit.getServer().getConsoleSender().sendMessage(getPrefix() + message);
	}
}
