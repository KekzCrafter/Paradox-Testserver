package de.kekz.testserver.manager.data;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import de.kekz.testserver.Main;
import de.kekz.testserver.manager.data.enums.DataInformation;
import de.kekz.testserver.manager.ranks.Rank;
import de.kekz.testserver.manager.verify.VerifySession;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class PlayerData {

	/**
	 * Player
	 */
	private Player player;

	/**
	 * Rank
	 */
	private Rank rank;

	/**
	 * Id
	 */
	private long id;

	/**
	 * Verify
	 */
	private VerifySession verifySession;
	private boolean verified;
	private long verifyTime = -2;

	private boolean updated = false;

	public PlayerData(Player player) {
		this.player = player;
	}

	public void unregister() {
		uploadCache();
		Main.getInstance().getPlayerDataManager().unregister(getPlayer());
	}

	public void uploadCache() {
		if (updated) {
			if (getVerifyTime() != -2L) {
				Main.getInstance().getVerifyManager().setVerificationTime(DataInformation.UUID,
						getUniqueId().toString(), getVerifyTime());
			}
			Main.getInstance().getVerifyManager().setDiscordId(DataInformation.UUID, getUniqueId().toString(), getId());
			Main.getInstance().getVerifyManager().setVerificationStatus(DataInformation.UUID, getUniqueId().toString(),
					isVerified());
			Main.getInstance().getVerifyManager().setRank(DataInformation.UUID, getUniqueId().toString(), getRank());
		}
	}

	public void loadCache() {
		this.rank = Main.getInstance().getVerifyManager().getRank(DataInformation.UUID, getUniqueId().toString());
		this.verified = Main.getInstance().getVerifyManager().getVerificationStatus(DataInformation.UUID,
				getUniqueId().toString());
		this.id = Main.getInstance().getVerifyManager().getDiscordId(DataInformation.UUID, getUniqueId().toString());
	}

	public void refresh() {
		getPlayer().setFoodLevel(20);
		getPlayer().setHealth(20D);
		getPlayer().setMaxHealth(20D);
		getPlayer().setLevel(0);
		getPlayer().setExp(0F);
		getPlayer().getInventory().setHeldItemSlot(0);
		getPlayer().setGameMode(GameMode.SURVIVAL);
		getPlayer().setAllowFlight(false);
		getPlayer().setFlying(false);
		for (PotionEffect effect : getPlayer().getActivePotionEffects()) {
			getPlayer().removePotionEffect(effect.getType());
		}

		teleportToSpawn();
	}

	public void updateScoreboard() {
		Main.getInstance().getScoreBoardManager().updateScoreboard(this, true);
	}

	/**
	 * Player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Rank
	 */
	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
		this.updated = true;
	}

	/**
	 * Id
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
		this.updated = true;
	}

	/**
	 * Verify
	 */
	public VerifySession createVerifySession(long sessionId) {
		this.verifySession = new VerifySession(sessionId);

		new BukkitRunnable() {

			@Override
			public void run() {
				if (getPlayer() == null || verifySession == null) {
					cancel();
					return;
				}

				verifySession = null;
				sendMessage(Main.getInstance().getPrefix() + ChatColor.RED + "Your verification session has expired.");
				cancel();
			}
		}.runTaskLater(Main.getInstance(), 20L * 60L);

		return this.verifySession;
	}

	public void acceptVerifySession() {
		setVerified(true);
		setId(getVerifySession().getSessionId());
		this.verifySession = null;

		Guild guild = Main.getInstance().getBotManager().getGuild();
		User user = Main.getInstance().getBotManager().getBot().getUserById(getId());
		Member member = guild.getMember(user);

		guild.addRoleToMember(member, guild.getRolesByName("Verified", true).get(0)).complete();
		sendPrivateMessage("Your account has been verified.");
	}

	public void removeVerifyStatus() {
		setVerified(false);

		Guild guild = Main.getInstance().getBotManager().getGuild();
		Member member = guild.getMember(Main.getInstance().getBotManager().getBot().getUserById(getId()));
		guild.removeRoleFromMember(member, guild.getRolesByName("Verified", true).get(0)).complete();
		sendPrivateMessage("Your verification status has been removed.");
	}

	public void cancelVerifySession() {
		this.verifySession = null;
	}

	public VerifySession getVerifySession() {
		return verifySession;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;

		if (verified) {
			setVerifyTime();
		} else {
			this.verifyTime = -1;
		}

		this.updated = true;
	}

	public long getVerifyTime() {
		return verifyTime;
	}

	public void setVerifyTime() {
		this.verifyTime = System.currentTimeMillis();
	}

	/**
	 * Other methods
	 */
	public void teleportToSpawn() {
		getPlayer().teleport(new Location(Bukkit.getWorld("Testworld"), 0D, 5.5D, 0D));
		getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
	}

	public String getName() {
		return getPlayer().getName();
	}

	public UUID getUniqueId() {
		return getPlayer().getUniqueId();
	}

	public World getWorld() {
		return getPlayer().getWorld();
	}

	public Location getLocation() {
		return getPlayer().getLocation();
	}

	public void sendPrivateMessage(String message) {
		Guild guild = Main.getInstance().getBotManager().getGuild();
		Member member = guild.getMember(Main.getInstance().getBotManager().getBot().getUserById(getId()));

		member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
	}

	public void sendMessage(String message) {
		getPlayer().sendMessage(message);
	}

	public void sendNoPermission() {
		sendMessage(Main.getInstance().getPrefix() + ChatColor.RED + "Invalid permissions!");
	}
}
