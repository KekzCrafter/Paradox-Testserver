package de.kekz.testserver.manager.verify;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import de.kekz.testserver.Main;
import de.kekz.testserver.manager.data.enums.DataInformation;
import de.kekz.testserver.manager.ranks.Rank;

public class VerifyManager {

	public void createTable() {
		try {
			Main.getInstance().getMySQL().update(
					"CREATE TABLE IF NOT EXISTS PlayerData (Name varchar(16), UUID varchar(50), DiscordId varchar(250), Rank varchar(50), verification_status varchar(10), verification_time bigint);");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void register(Player player) {
		if (Main.getInstance().getMySQL().isConnected()) {
			try {
				if (!isRegistered(DataInformation.UUID, player.getUniqueId().toString())) {
					Main.getInstance().getMySQL().update(
							"INSERT INTO PlayerData (Name, UUID, DiscordId, Rank, verification_status, verification_time) VALUES ('"
									+ player.getName() + "', '" + player.getUniqueId()
									+ "', '-1', 'User', 'false', '-1');");
				} else {
					String currentUUID = player.getUniqueId().toString(),
							name = getName(DataInformation.UUID, currentUUID);
					if (!name.equals(player.getName())) {
						setName(DataInformation.UUID, currentUUID, player.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isRegistered(DataInformation info, Object value) {
		try {
			ResultSet rs = Main.getInstance().getMySQL()
					.getResult("SELECT * FROM PlayerData WHERE " + info.getName() + "='" + value + "'");

			if (rs.next()) {
				return rs.getString(info.getName()) != null;
			}
			rs.close();

			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Object getResult(String select, DataInformation info, String value) {
		try {
			PreparedStatement ps = Main.getInstance().getMySQL().con
					.prepareStatement("SELECT * FROM PlayerData WHERE " + info.getName() + "='" + value + "'");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				return rs.getObject(select);
			}
			ps.close();
			rs.close();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Data
	 */
	public String getName(DataInformation info, String value) {
		return String.valueOf(getResult("Name", info, value));
	}

	public void setName(DataInformation info, String value, String name) {
		Main.getInstance().getMySQL()
				.update("UPDATE PlayerData SET Name='" + name + "' WHERE " + info.getName() + "='" + value + "'");
	}

	public String getUniqueId(DataInformation info, String value) {
		return String.valueOf(getResult("UUID", info, value));
	}

	public long getDiscordId(DataInformation info, String value) {
		return Long.valueOf(String.valueOf(getResult("DiscordId", info, value)));
	}

	public void setDiscordId(DataInformation info, String value, long id) {
		Main.getInstance().getMySQL()
				.update("UPDATE PlayerData SET DiscordId='" + id + "' WHERE " + info.getName() + "='" + value + "'");
	}

	public boolean getVerificationStatus(DataInformation info, String value) {
		return Boolean.valueOf(String.valueOf(getResult("verification_status", info, value)));
	}

	public void setVerificationStatus(DataInformation info, String value, boolean status) {
		Main.getInstance().getMySQL().update("UPDATE PlayerData SET verification_status='" + status + "' WHERE "
				+ info.getName() + "='" + value + "'");
	}

	public boolean getVerificationTime(DataInformation info, String value) {
		return Boolean.valueOf(String.valueOf(getResult("verification_time", info, value)));
	}

	public void setVerificationTime(DataInformation info, String value, long time) {
		Main.getInstance().getMySQL().update(
				"UPDATE PlayerData SET verification_time='" + time + "' WHERE " + info.getName() + "='" + value + "'");
	}

	public Rank getRank(DataInformation info, String value) {
		return Rank.getRankByName(String.valueOf(getResult("Rank", info, value)));
	}

	public void setRank(DataInformation info, String value, Rank rank) {
		Main.getInstance().getMySQL().update(
				"UPDATE PlayerData SET Rank='" + rank.getName() + "' WHERE " + info.getName() + "='" + value + "'");
	}
}
