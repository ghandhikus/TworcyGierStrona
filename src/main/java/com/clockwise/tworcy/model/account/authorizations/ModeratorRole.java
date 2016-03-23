package com.clockwise.tworcy.model.account.authorizations;

import org.springframework.security.core.GrantedAuthority;

import com.clockwise.tworcy.model.account.Access;

public class ModeratorRole implements GrantedAuthority {
	private static final long serialVersionUID = 5528063967544116495L;

	public ModeratorRole() {
	}

	@Override
	public String getAuthority() {
		return Access.MODERATOR.toString();
	}

}
