package com.clockwise.tworcy.model.account;

import static org.springframework.util.Assert.notNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.clockwise.tworcy.model.account.authorizations.AdminRole;
import com.clockwise.tworcy.model.account.authorizations.HeadAdminRole;
import com.clockwise.tworcy.model.account.authorizations.ModeratorRole;
import com.clockwise.tworcy.model.account.authorizations.NormalRole;

public @Transactional @Repository class AccountDAOHibernate implements AccountDAO {
	// Hibernate sessions
	private @Autowired SessionFactory sessionFactory;
	/** Annotated table in {@link AccountData} */
	private static String accountDataTable;
	/** Statement for get() */
	private static String getStatement;
	/** Statement for get(Username) */
	private static String getByUsernameStatement;
	
	/** Catches table of AccountData */
	static {
		accountDataTable = AccountData.class.getAnnotation(Table.class).name();
		getStatement = "from "+accountDataTable;
		getByUsernameStatement = getStatement + " where username=:username";
	}
	
	/** Quick hibernate session catcher */
	private Session getSession(){ return sessionFactory.getCurrentSession(); }

	/** Logging library instance for this class */
	private static final Logger logger = Logger.getLogger(AccountDAOHibernate.class);
	
	public @Override Account create(String username, String password) {
		// Check method params
		notNull(username);
		notNull(password);
		
		// Create data object
		AccountData data = new AccountData();
		data.setUsername(username);
		data.setPassword(password);
		data.setAccess((byte)Access.NORMAL.getAccess());
		data.setEnabled(true);
		data.setLocked(false);
		
		// Insert to db
		getSession().persist(data);
		
		// Catch the data from db
		return get(username);
	}

	public @Override Account get(Integer id) {
		// Check method params
		notNull(id);

		// Catch the data and convert it to the account
		AccountData data = (AccountData) getSession().load(AccountData.class, id);
		return convert(data);
	}

	/**
	 * Converts the list of {@link AccountData} to {@link Account Accounts}
	 * @param data list which contains {@link AccountData}
	 * @return list containing converted {@link Account Accounts}
	 */
	private List<Account> convert(List<AccountData> data) {
		// Holding array
		ArrayList<Account> accounts = new ArrayList<>(data.size());
		// Converting loop
		for(int i=0;i<data.size();i++)
			accounts.set(i, convert(data.get(i)));
		// Return
		return accounts;
	}
	
	/**
	 * Converts {@link AccountData} to {@link Account}
	 * @param data object containing raw information about the {@link Account}
	 * @return converted {@link Account}
	 */
	private Account convert(AccountData data) {
		// Param checks
		notNull(data);
		
		// Converting Access to Spring.Security RememberMe format
		Collection<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
		byte access = data.getAccess();
		if(access >= Access.NORMAL.getAccess()) auth.add(new NormalRole());
		if(access >= Access.MODERATOR.getAccess()) auth.add(new ModeratorRole());
		if(access >= Access.ADMIN.getAccess()) auth.add(new AdminRole());
		if(access >= Access.HEADADMIN.getAccess()) auth.add(new HeadAdminRole());
		
		// Return of created account object
		return new Account(data.getId(), data.getUsername(), data.getPassword(), data.getLastLogin(), data.getLastPasswordChange(), data.getExpireOn(), data.getPasswordExpireOn(), data.isEnabled(), data.isLocked(), auth);
	}
	
	/**
	 * Converts {@link Account} back to raw {@link AccountData}
	 * @param acc {@link Account}
	 * @return converted {@link AccountData}
	 */
	private AccountData convertBack(Account acc) {
		// Raw data object
		AccountData data = new AccountData();
		
		// Populating data
		data.setId(acc.getId());
		data.setUsername(acc.getName());
		data.setPassword(acc.getPassword());
		data.setAccess(acc.getAccessLevel().getAccess());
		data.setLastLogin(acc.getLastLogin());
		data.setLastPasswordChange(acc.getLastPasswordChange());
		data.setExpireOn(acc.getExpireOn());
		data.setPasswordExpireOn(acc.getPasswordExpireOn());
		data.setEnabled(acc.isEnabled());
		data.setLocked(!acc.isAccountNonLocked());
		
		// Return
		return data;
	}
	
	public @Override Account get(String username) {
		// Param checks
		if(username == null) return null;
		if(username.length()==0) return null;
		
		// Statement for 
		Query query = getSession().createQuery(getByUsernameStatement);
		query.setParameter("username", username);
		
		AccountData data = (AccountData) query.uniqueResult();
		return convert(data);
	}

	@Override
	public List<Account> getList(Integer count, Integer offset) {
		
		if(count == null || count == 0) count = 10;
		if(offset == null) offset = 0;

        Query query = getSession().createQuery(getStatement)
        		.setFirstResult(offset)
        		.setMaxResults(count);
        
		@SuppressWarnings("unchecked")
		List<AccountData> accounts = (List<AccountData>) query.list();
        
        return convert(accounts);
	}

	@Override
	public void delete(Integer id) {
		if(id == null) return;
		getSession().delete(get(id));
		// Debug
		logger.debug("Removed Record from Players where id = " + id);
	}

	@Override
	public void update(Account account) {
		AccountData data = convertBack(account);
		getSession().update(data);
		// Debug
		logger.debug("Updated Record with id = " + account.getId());
	}
}
