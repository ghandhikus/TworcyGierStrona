package com.clockwise.tworcy.model.account;

import java.util.Collection;

import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class Account extends User {

	public Account(Integer id, String username, String password, DateTime lastLogin, DateTime lastPasswordChange, DateTime expireOn, DateTime passwordExpireOn,
			boolean enabled,
			boolean accountLocked,
			Collection<? extends GrantedAuthority> authorities)
	{
		// Call the super
		super(username, password, enabled,
				// Check if database expire on is null
				(expireOn != null) ? expireOn.isAfterNow(): true,
				// Null check
				(passwordExpireOn!=null) ? passwordExpireOn.isAfterNow() : true,
				!accountLocked, authorities);
		
		this.id = id;
		this.lastLogin = lastLogin;
		this.lastPasswordChange = lastPasswordChange;
		this.expireOn = expireOn;
		this.passwordExpireOn = passwordExpireOn;
	}

	private static final long serialVersionUID = 4590436604483525434L;

	private final Integer id;
	private Access accessLevel = Access.NORMAL;
	private final DateTime lastLogin;
	private final DateTime lastPasswordChange;
	private final DateTime expireOn;
	private final DateTime passwordExpireOn;

	public static boolean namePatternMatch(String name) {
		return name.matches("^[a-zA-Z0-9_]*$");
	}

	public boolean nameLengthMatch(String name) {
		return (name.length() >= 6 && name.length() <= 16);
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return getUsername();
	}

	public Access getAccessLevel() {
		return accessLevel;
	}

	public int getAccess() {
		return accessLevel.getAccess();
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id: ").append(this.id).append("; ");
		sb.append(super.toString());
		sb.append("Last Login: ").append(this.lastLogin).append("; ");
		sb.append("Last Password Change: ").append(this.lastPasswordChange).append("; ");
		sb.append("Expires on: ").append(this.expireOn).append("; ");
		sb.append("Password expires on: ").append(this.passwordExpireOn).append("; ");
		sb.append("StringAuthorities : { ");
		for(GrantedAuthority auth : this.getAuthorities())
			sb.append(auth.getAuthority()+" ");
		sb.append("}");
		
		return sb.toString();
	}
}
