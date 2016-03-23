package com.clockwise.tworcy.model.account.authorizations;

import org.springframework.security.core.GrantedAuthority;

import com.clockwise.tworcy.model.account.Access;

public class NormalRole implements GrantedAuthority {
	private static final long serialVersionUID = 4330252853775168372L;

	@Override
	public String getAuthority() {
		return Access.NORMAL.toString();
	}

}
