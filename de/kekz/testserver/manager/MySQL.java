package de.kekz.testserver.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.kekz.testserver.Main;

public class MySQL {

	private ExecutorService executorservice;
	public String username;
	public String password;
	public String database;
	public String host;
	public String port;
	public Connection con;

	public void createConnection() {
		if (!isConnected()) {
			try {
				executorservice = Executors.newCachedThreadPool();
				con = DriverManager.getConnection(
						"jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username,
						password);

				Main.getInstance().log("§aDatabase connection created...");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void closeConnection() {
		if (isConnected()) {
			try {
				con.close();

				Main.getInstance().log("§aDatabase connection closed...");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isConnected() {
		return con != null;
	}

	public void update(String statement) {
		executorservice.execute(() -> queryUpdate(statement));
	}

	public ResultSet getResult(String qry) {
		if (isConnected()) {
			try {
				return con.createStatement().executeQuery(qry);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void queryUpdate(String query) {
		if (isConnected()) {
			try (PreparedStatement statement = con.prepareStatement(query)) {
				queryUpdate(statement);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void queryUpdate(PreparedStatement statement) {
		if (isConnected()) {
			try {
				statement.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					statement.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean validValues() {
		return !this.username.equals("None") && !this.password.equals("None") && !this.database.equals("None")
				&& !this.host.equals("None");
	}
}
