package com.clockwise.tworcy.model.account.authorizations;

import org.springframework.security.core.GrantedAuthority;

import com.clockwise.tworcy.model.account.Access;

public class HeadAdminRole implements GrantedAuthority {
	private static final long serialVersionUID = -8823605445072143546L;

	public HeadAdminRole() {
	}

	@Override
	public String getAuthority() {
		return Access.HEADADMIN.toString();
	}

}
