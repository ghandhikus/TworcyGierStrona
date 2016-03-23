package com.clockwise.tworcy.model.account;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import com.clockwise.tworcy.util.Messages;
import com.clockwise.tworcy.util.PasswordHasher;

public @Service("userDetailsService") class AccountServiceJDBC implements UserDetailsService, AccountService {

	private @Autowired AccountDAO db;
	private @Autowired PasswordHasher hasher;
	private @Autowired AuthenticationProvider auth;

	public @Override void update(Account player) {
		db.update(player);
	}

	public @Override void delete(Integer id) {
		db.delete(id);
	}

	public @Override List<Account> getList() {
		return db.getList();
	}

	public @Override Account get(Integer id) {
		return db.get(id);
	}
	
	public @Override Account get(String name) {
		return db.get(name);
	}
	public @Override boolean isLogged() {
		return getLogged()!=null;
	}

	public @Override Account getLogged() {		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth == null) {
			//System.out.println("AccountService auth is null");
			return null;
		}
		Object principal = auth.getPrincipal();
		
		if(principal instanceof Account) {
			Account acc = (Account) principal;
			System.out.println("Account found: "+acc.getName());
			return acc;
		} else if(principal instanceof String) {
			// Not authenticated
			return null;
		}
		else if (principal == null)
		{
			// Shouldn't
			System.out.println("AccountService principal is null");
			return null;
		} else {
			System.out.println("AccountService principal type is: "+principal.getClass());
		}
		return null;
	}
	
	public @Override void logout() {
		SecurityContextHolder.clearContext();
	}
	
	public @Override Account login(String username, String password, HttpServletRequest request) throws Exception
	{
		// Try to catch player
		Account account = get(username);
		// Check if password matches
		if (account != null && !hasher.matches(password, account.getPassword()))
			throw new Exception(Messages.getString("Account.passNotMatch")); //$NON-NLS-1$
		// Check if account exists
		else if(account == null)
			throw new Exception(Messages.getString("Account.accNotExist")); //$NON-NLS-1$
		// Logging in
		else
		{
			// TODO: Rehash the password and update it in database
			//account.setPasswordHash(hash(password));
			//dao.update(account);
			
			// Log in
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, hasher.hash(password));
			token.setDetails(new WebAuthenticationDetails(request));
	        Authentication authentication = auth.authenticate(token);
	        SecurityContextHolder.getContext().setAuthentication(authentication);
			
			// Success
			return account;
		}
	}
	
	/*
	public boolean isLogged(HttpSession session)
	{
		return session.getAttribute("account")!=null;  //$NON-NLS-1$
	}
	
	public @Override Account getLogged(HttpSession session)
	{
		return (Account) session.getAttribute("account");  //$NON-NLS-1$
	}
	
	public @Override Account login(String name, String password, HttpSession session) throws Exception
	{
		// Try to catch player
		Account account = get(name);
		// Check if password matches
		if (account != null && !checkPasswordHash(password, account.getPasswordHash()))
			throw new Exception(Messages.getString("Account.passNotMatch")); //$NON-NLS-1$
		// Check if account exists
		else if(account == null)
			throw new Exception(Messages.getString("Account.accNotExist")); //$NON-NLS-1$
		// Logging in
		else
		{
			// Rehash the password and update it in database
			account.setPasswordHash(hash(password));
			dao.update(account);
			
			// Log in
			session.setAttribute("account", account);
			
			// Success
			return account;
		}
	}
	
	public @Override void logout(HttpSession session)
	{
		if (isLogged(session)) {
			update(getLogged(session));
		}
		session.removeAttribute("account");
	}
	*/
	public @Override Account register(String username, String password) throws Exception {

		List<String> errors = new ArrayList<>();

		// Checks if form data is specified correctly
		// Name checks
		if (username == null)
			errors.add(Messages.getString("Account.accNotSet")); //$NON-NLS-1$
		else if (!(username.length() >= 6))
			errors.add(Messages.getString("Account.accTooShort")); //$NON-NLS-1$
		else if (!(username.length() <= 32))
			errors.add(Messages.getString("Account.accTooLong")); //$NON-NLS-1$
		else if (!Account.namePatternMatch(username))
			errors.add(Messages.getString("Account.accBadPattern")); //$NON-NLS-1$

		// Password checks
		if (password == null)
			errors.add(Messages.getString("Account.passNotSet")); //$NON-NLS-1$
		else if (!(password.length() >= 6))
			errors.add(Messages.getString("Account.passTooShort")); //$NON-NLS-1$
		else if (!(password.length() <= 128))
			errors.add(Messages.getString("Account.passTooLong")); //$NON-NLS-1$

		// Just a data holder for db.get
		Account account = null;
		// Check if user is already registered
		if (errors.size() == 0)
			if ((account = db.get(username)) != null)
				errors.add(Messages.getString("Account.accAlreadyRegistered")); //$NON-NLS-1$

		// If no errors till now then go further
		if (errors.size() == 0) {
			// Check if player is in db
			if (account == null) {
				// new Player
				db.create(username, hasher.hash(password));
			}
			
		}

		String error = Messages.getString("Account.cantLogin"); //$NON-NLS-1$
		for (String err : errors)
			error += "<br />" + err; //$NON-NLS-1$
		
		if(errors.size() > 0)
		{
			//logout(session);
			throw new Exception(error);
		}
		
		return db.get(username);
	}
	
	public @Override UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		Account account = get(name);
		
		if(account == null)
			throw new UsernameNotFoundException("Can't find user with name: " + name);
		else
			return account;
	}
}
