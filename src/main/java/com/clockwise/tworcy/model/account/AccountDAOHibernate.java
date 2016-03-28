package com.clockwise.tworcy.model.account;

import java.util.List;

import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public @Transactional @Repository class AccountDAOHibernate implements AccountDAO {
	/** Hibernate sessions */
	private @Autowired SessionFactory sessionFactory;
	private @Autowired AccountConverter convert;
	/** Annotated table in {@link AccountData} */
	private String accountDataTable;
	/** Statement for get() */
	private String getStatement;
	/** Statement for get(Username) */
	private String getByUsernameStatement;
	
	/** Catches table of AccountData */
	{
		accountDataTable = AccountData.class.getDeclaredAnnotation(Table.class).name();
		getStatement = "from "+accountDataTable;
		getByUsernameStatement = getStatement + " where username=:username";
	}
	
	/** Quick hibernate session catcher */
	private Session getSession(){ return sessionFactory.getCurrentSession(); }

	/** Logging library instance for this class */
	private static final Logger logger = Logger.getLogger(AccountDAOHibernate.class);
	
	public @Override Account create(String username, String password) {
		// Check method params
		if(username == null || username.equals("")) return null;
		if(password == null || password.equals("")) return null;
		
		// Create data object
		AccountData data = new AccountData();
		data.setUsername(username);
		data.setPassword(password);
		data.setAccess((byte)Access.NORMAL.getAccess());
		data.setEnabled(true);
		data.setLocked(false);
		
		// Insert to db
		getSession().persist(data);
		
		logger.debug("Create account : "+data.toString());
		
		// Catch the data from db
		return get(username);
	}

	public @Override Account get(Integer id) {
		// Check method params
		if(id == null || id == 0) return null;

		// Catch the data and convert it to the account
		AccountData data = (AccountData) getSession().load(AccountData.class, id);
		logger.debug("Get account by id : "+data.toString());
		return convert.convert(data);
	}

	
	public @Override Account get(String username) {
		// Param checks
		if(username == null) return null;
		if(username.length()==0) return null;
		
		// Statement for 
		Query query = getSession().createQuery(getByUsernameStatement);
		query.setParameter("username", username);
		
		AccountData data = (AccountData) query.uniqueResult();
		logger.debug("Get account by username : "+data.toString());
		return convert.convert(data);
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
		
		// Debug loop
        if(logger.isDebugEnabled())
        	for(int i=0;i<accounts.size();i++)
        		if(accounts.get(i) != null)
        			logger.debug("Get list["+i+"] : "+accounts.get(i).toString());
        
        // Return converted list of accounts
        return convert.convert(accounts);
	}

	@Override
	public void delete(Integer id) {
		if(id == null) return;
		getSession().delete(get(id));
		// Debug
		logger.debug("Delete id = " + id);
	}

	@Override
	public void update(Account account) {
		AccountData data = convert.convertBack(account);
		getSession().update(data);
		// Debug
		logger.debug("Update account = " + data.toString());
	}
}
