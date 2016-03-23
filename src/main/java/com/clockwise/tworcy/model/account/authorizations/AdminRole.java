package com.clockwise.tworcy.model.account.authorizations;

import org.springframework.security.core.GrantedAuthority;

import com.clockwise.tworcy.model.account.Access;

public class AdminRole implements GrantedAuthority {
	private static final long serialVersionUID = 6215463299981262506L;

	public AdminRole() {
	}

	@Override
	public String getAuthority() {
		return Access.ADMIN.toString();
	}

}
