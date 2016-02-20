package com.clockwise.model.account;

public class Account {
	private Integer id;
	private String name;
	private String passwordhash;
	private Access accessLevel = Access.NORMAL;

	public boolean isCorrect() {
		return (nameLengthMatch(name) && namePatternMatch(name) && passwordhash != null && passwordhash.length() != 0);
	}

	public static boolean namePatternMatch(String name) {
		return name.matches("^[a-zA-Z0-9_]*$");
	}

	public boolean nameLengthMatch(String name) {
		return (name.length() >= 6 && name.length() <= 16);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Access getAccessLevel() {
		return accessLevel;
	}

	public int getAccess() {
		return accessLevel.getAccess();
	}

	/**
	 * Default access only.
	 */
	void setAccessLevel(Access accessLevel) {
		this.accessLevel = accessLevel;
	}

	void setName(String name) {
		this.name = name;
	}

	void setPasswordHash(String passwordhash) {
		this.passwordhash = passwordhash;
	}

	String getPasswordHash() {
		return passwordhash;
	}

}
