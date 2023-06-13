package de.kekz.testserver.manager.data.enums;

public enum DataInformation {

	NAME("Name"), UUID("UUID");

	private String name;

	private DataInformation(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
