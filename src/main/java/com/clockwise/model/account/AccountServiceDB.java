package com.clockwise.model.account;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.clockwise.util.Messages;
import com.clockwise.util.PasswordHasher;

@Component
public class AccountServiceDB implements AccountService {

	private volatile int newUserCount = 0;
	@Autowired AccountRepository dao;
	@Autowired PasswordHasher hasher;
	
	@Override
	public void insert(Account player, String name, String hash) {
		newUserCount++;
		player.setName(name);
		player.setPasswordHash(hash);
		dao.insert(player);
	}

	@Override
	public void update(Account player) {
		dao.update(player);
	}

	@Override
	public void delete(Integer id) {
		dao.delete(id);
	}

	@Override
	public List<Account> getList() {
		return dao.getList();
	}

	@Override
	public Account get(Integer id) {
		return dao.get(id);
	}
	@Override
	public Account get(String name) {
		return dao.get(name);
	}

	@Override
	public Integer getNewUserCount() {
		return newUserCount;
	}


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
	
	private String hash(String base) {
		return hasher.hash(base);
	}
	private boolean checkPasswordHash(String password, String hash) {
		return hasher.matches(password, hash);
	}
	
	@Override
	public Account register(String name, String password, HttpSession session) throws Exception {

		List<String> errors = new ArrayList<>();

		// Checks if form data is specified correctly
		// Name checks
		if (name == null)
			errors.add(Messages.getString("Account.accNotSet")); //$NON-NLS-1$
		else if (!(name.length() >= 6))
			errors.add(Messages.getString("Account.accTooShort")); //$NON-NLS-1$
		else if (!(name.length() <= 32))
			errors.add(Messages.getString("Account.accTooLong")); //$NON-NLS-1$
		else if (!Account.namePatternMatch(name))
			errors.add(Messages.getString("Account.accBadPattern")); //$NON-NLS-1$

		// Password checks
		if (password == null)
			errors.add(Messages.getString("Account.passNotSet")); //$NON-NLS-1$
		else if (!(password.length() >= 6))
			errors.add(Messages.getString("Account.passTooShort")); //$NON-NLS-1$
		else if (!(password.length() <= 128))
			errors.add(Messages.getString("Account.passTooLong")); //$NON-NLS-1$

		Account account = null;
		// Check if user is already registered
		if (errors.size() == 0)
			if ((account = get(name)) != null)
				errors.add(Messages.getString("Account.accAlreadyRegistered")); //$NON-NLS-1$

		// If no errors till now then go further
		if (errors.size() == 0) {
			// Check if player is in db
			if (account == null) {
				// new Player
				account = new Account();
				insert(account, name, hash(password));
				login(name, password, session);
			}
			
		}

		String error = Messages.getString("Account.cantLogin"); //$NON-NLS-1$
		for (String err : errors)
			error += "<br />" + err; //$NON-NLS-1$
		
		if(errors.size() > 0)
		{
			logout(session);
			throw new Exception(error);
		}
		
		return getLogged(session);
	}
}
