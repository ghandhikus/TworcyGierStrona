package com.clockwise.tworcy.model.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.DateTime;

/**
 * Represents Account data in database
 * @author Daniel
 */
@Entity
@Table(name = "AccountData")
public class AccountData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;
	
	@Column(name = "username", nullable = false)
	private String username;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	@Column(name = "access", nullable = false)
	private byte access;
	
	@Column(name = "lastlogin")
	private DateTime lastLogin;
	
	@Column(name = "lastpasswordchange")
	private DateTime lastPasswordChange;
	
	@Column(name = "expireon")
	private DateTime expireOn;
	
	@Column(name = "passwordexpireon")
	private DateTime passwordExpireOn;
	
	@Column(name = "enabled", nullable = false)
	private boolean enabled;
	
	@Column(name = "locked", nullable = false)
	private boolean locked;
	
	public int getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public byte getAccess() {
		return access;
	}

	public DateTime getLastLogin() {
		return lastLogin;
	}
	
	public DateTime getLastPasswordChange() {
		return lastPasswordChange;
	}
	
	public DateTime getExpireOn() {
		return expireOn;
	}
	
	public DateTime getPasswordExpireOn() {
		return passwordExpireOn;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isLocked() {
		return locked;
	}
	

	void setId(int id) {
		this.id = id;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setAccess(byte access) {
		this.access = access;
	}
	
	public void setLastLogin(DateTime lastLogin) {
		this.lastLogin = lastLogin;
	}
	
	public void setLastPasswordChange(DateTime lastPasswordChange) {
		this.lastPasswordChange = lastPasswordChange;
	}
	
	public void setExpireOn(DateTime expireOn) {
		this.expireOn = expireOn;
	}
	
	public void setPasswordExpireOn(DateTime passwordExpireOn) {
		this.passwordExpireOn = passwordExpireOn;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
