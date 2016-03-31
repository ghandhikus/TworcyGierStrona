package com.clockwise.tworcy.model.account;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clockwise.tworcy.model.news.News;

@Repository class AccountDAOHibernate implements AccountDAO {
	/** Hibernate sessions */
	private @Autowired SessionFactory sessionFactory;
	private @Autowired AccountConverter convert;
	/** Annotated table in {@link AccountData} */
	private final String accountTableFieldUsername;
	private final String accountTableFieldId;
	
	
	/** Catches table of AccountData 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException
	 */
	public AccountDAOHibernate() throws NoSuchFieldException, SecurityException {
		accountTableFieldId = AccountData.class.getDeclaredField("id").getName();
		accountTableFieldUsername = AccountData.class.getDeclaredField("username").getName();
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
		getSession().save(data);
		
		logger.debug("Create account : "+data.toString());
		
		return convert.convert(data);
	}

	public @Override Account get(Integer id) {
		// Check method params
		if(id == null || id == 0) return null;

		return convert.convert(getData(id));
	}
	
	public AccountData getData(Integer id) {

		try {
			Criteria criteria = getSession().createCriteria(AccountData.class).add(Restrictions.eq(accountTableFieldId, id));

			Object obj = criteria.uniqueResult();
			if(obj == null)
				return null;
			else {
				AccountData data = (AccountData) obj;
				return data;
			}
		} catch(HibernateException e) {
			logger.warn(e);
			return null;
		}
	}

	
	public @Override Account get(String username) {
		// Param checks
		if(username == null) return null;
		if(username.length()==0) return null;
		
		Criteria criteria = getSession().createCriteria(AccountData.class).add(Restrictions.eq(accountTableFieldUsername, username));

		try {
			Object obj = criteria.uniqueResult();
			if(obj == null)
				return null;
			else {
				AccountData data = (AccountData) obj;
				logger.debug("Get account by username : "+data.toString());
				return convert.convert(data);
			}
			} catch(HibernateException e) {
				logger.warn(e);
				return null;
			}
	}

	@Override
	public List<Account> getList(Integer count, Integer offset) {
		if(count == null || count == 0) count = 10;
		if(offset == null) offset = 0;

		try {
			Criteria crit = getSession().createCriteria(News.class);
			crit.setMaxResults(count);
			crit.setFirstResult(offset);
			crit.addOrder(Order.desc(this.accountTableFieldId));
	        
			@SuppressWarnings("unchecked")
			List<AccountData> accounts = (List<AccountData>) crit.list();
			
			// Debug loop
	        if(logger.isDebugEnabled())
	        	for(int i=0;i<accounts.size();i++)
	        		if(accounts.get(i) != null)
	        			logger.debug("Get list["+i+"] : "+accounts.get(i).toString());
	        
	        // Return converted list of accounts
	        return convert.convert(accounts);
		} catch ( HibernateException e ) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public void delete(Integer id) {
		if(id == null) return;
		getSession().delete(getData(id));
		// Debug
		logger.debug("Delete id = " + id);
	}

	@Override
	public void update(Account account) {
		// Get current object
		AccountData data = getData(account.getId());
		// Update data
		convert.convertBack(account, data);
		// Update database
		getSession().update(data);
		// Debug
		logger.debug("Update account = " + data.toString());
	}
}
