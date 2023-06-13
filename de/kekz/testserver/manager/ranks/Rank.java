package de.kekz.testserver.manager.ranks;

import org.bukkit.ChatColor;

public enum Rank {

	AUTHOR("Author", ChatColor.RED, 3, 0), 
	SUPPORT("Support", ChatColor.YELLOW, 2, 1), 
	FRIEND("Friend", ChatColor.GOLD, 1, 2), 
	DEVELOPER("Developer", ChatColor.DARK_PURPLE, 1, 3),
	USER("User", ChatColor.GRAY, 0, 4);

	private String name;
	private ChatColor color;
	private int permission;
	private int id;

	private Rank(String name, ChatColor color, int permission, int id) {
		this.name = name;
		this.color = color;
		this.permission = permission;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public ChatColor getColor() {
		return color;
	}

	public int getPermission() {
		return permission;
	}
	
	public int getId() {
		return id;
	}
	
	public static Rank getRankByName(String name) {
		for(Rank ranks : values()) {
			if(ranks.getName().equalsIgnoreCase(name)) {
				return ranks;
			}
		}
		
		return null;
	}
}
