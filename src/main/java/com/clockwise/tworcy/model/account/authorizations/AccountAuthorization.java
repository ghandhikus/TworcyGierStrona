package com.clockwise.tworcy.model.account.authorizations;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

public @Service class AccountAuthorization implements AuthenticationProvider {


	private static final Logger logger = Logger.getLogger(AccountAuthorization.class);
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		logger.debug("AccountAuthorization.authenticate("+authentication.toString()+")");
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		logger.debug("AccountAuthorization.supports("+authentication.toString()+")");
		return (authentication.isAssignableFrom(ModeratorRole.class));
	}

}
