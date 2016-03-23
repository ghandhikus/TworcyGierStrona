package com.clockwise.tworcy.model.account.authorizations;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

public @Service class AccountAuthorization implements AuthenticationProvider {

	{
		System.out.println("AccountAuthorization.construct");
	}
	
	static {
		System.out.println("AccountAuthorization.static_construct");
	}
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		System.out.println("AccountAuthorization.authenticate("+authentication.toString()+")");
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		System.out.println("AccountAuthorization.supports("+authentication.toString()+")");
		return (authentication.isAssignableFrom(ModeratorRole.class));
	}

}
