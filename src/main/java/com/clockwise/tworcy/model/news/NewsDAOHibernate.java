package com.clockwise.tworcy.model.news;

import java.util.List;

import javax.persistence.Column;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.clockwise.tworcy.model.account.AccountService;

/**
 * Implementation of {@link NewsDAO} that uses hibernate.
 * <br>
 * Parameters are bullet proof, they don't throw exceptions when some of the parameters is null.
 * <br>
 * If comment didn't specify on how it accepts the parameters,
 * then please check the source to check how parameters will react for nulls.
 * <br>
 * News parameters are not html escaped. It is just plain object for database operations.
 * @author Daniel
 */
@Transactional @Repository class NewsDAOHibernate implements NewsDAO {
	// Hibernate sessions
	private @Autowired SessionFactory sessionFactory;
	private @Autowired AccountService accounts;

	// Holds column names inside the News table.
	private String newsTableFieldId;
	private String newsTableFieldAuthorId;
	private String newsTableFieldDate;
	
	/**
	 * Populates internal strings for column names.
	 * @throws Exception occurs when reflection fails to detect column names from News using the {@link Column} annotation
	 */
	public NewsDAOHibernate() throws Exception {
		try {
			newsTableFieldId = News.class.getDeclaredField("newsId").getName();
			newsTableFieldAuthorId = News.class.getDeclaredField("authorId").getName();
			newsTableFieldDate = News.class.getDeclaredField("date").getName();

			logger.debug("newsTableFieldId : "+newsTableFieldId);
			logger.debug("newsTableFieldAuthorId : "+newsTableFieldAuthorId);
			logger.debug("newsTableFieldDate : "+newsTableFieldDate);
		} catch (NoSuchFieldException | SecurityException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	/** Quick hibernate session catcher */
	private Session getSession() { return sessionFactory.getCurrentSession(); }
	
	/** Logging library instance for this class */
	private static final Logger logger = Logger.getLogger(NewsDAOHibernate.class);
	
	public @Override News insert(News news) {
		// Check news parameters
		if (news == null) return null;
		if (news.getDate() == null) return null;
		if (news.getTitle() == null) return null;
		if (news.getContent() == null) return null;
		
		getSession().persist(news);
		
		// Return news with changed id
		return getByAuthorId(news.getAuthorId());
	}

	public @Override void update(News news) {
		if (news == null) return;
		if (news.getDate() == null) return;
		if (news.getTitle() == null) news.setTitle("null");		// Failsafety
		if (news.getContent() == null) news.setContent("null"); // Failsafety

		getSession().update(news);
	}

	public @Override void delete(News news) {
		if (news == null) return;

		getSession().delete(news);
	}

	public @Override int getCount() {
		return ((Number)getSession().createCriteria(News.class).setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	public @Override News getSpecific(Integer newsId) {
		if (newsId == null) return null;
		return (News) getSession().createCriteria(News.class).add(Restrictions.eq(newsTableFieldId, newsId)).uniqueResult();
	}
	
	public @Override News getByAuthorId(Integer authorId) {
		if (authorId == null) return null;
		return (News) getSession().createCriteria(News.class).add(Restrictions.eq(newsTableFieldAuthorId, authorId)).uniqueResult();
		
	}

	@SuppressWarnings("unchecked")
	public @Override List<News> getUserNews(Integer accountId, Integer count, Integer offset) {
		if (accountId == null) return null;
		if (count == null) count = 10;
		if (offset == null) offset = 0;
		if (count > 1000) count = 1000;
		
		int newsCount = getCount();
		// Make sure that offset won't be higher than newsCount
		if (offset > newsCount) offset = newsCount - 1;
		// There is no news in db so no need to get anything
		if (newsCount == 0) return null;

		try {
			Criteria crit = getSession().createCriteria(News.class);
			crit.setMaxResults(count);
			crit.setFirstResult(offset);
			crit.add(Restrictions.eq(newsTableFieldAuthorId, accountId));
			crit.addOrder(Order.desc(newsTableFieldDate));
			crit.addOrder(Order.desc(newsTableFieldId));
			return (List<News>) crit.list();
		} catch ( HibernateException e ) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public @Override List<News> getRecent(Integer count, Integer offset) {
		if (count == null) count = 10;
		if (offset == null) offset = 0;
		if (count > 1000) count = 1000;
		
		int newsCount = getCount();
		// Make sure that offset won't be higher than newsCount
		if (offset > newsCount) offset = newsCount - 1;
		// There is no news in db so no need to get anything
		if (newsCount == 0) return null;

		
		try {
			Criteria crit = getSession().createCriteria(News.class);
			crit.setMaxResults(count);
			crit.setFirstResult(offset);
			crit.addOrder(Order.desc(newsTableFieldDate));
			crit.addOrder(Order.desc(newsTableFieldId));
			List<News> list = (List<News>) crit.list();
			for(int i=0;i<list.size();i++) {
				logger.debug("News recent["+i+"] = "+list.get(i).toString());
			}
			return list;
		} catch ( HibernateException e ) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public @Override void archive(News news) {
		if (news == null) return;
		
		// Begin transaction between tables
		Transaction transaction = getSession().getTransaction();
		transaction.begin();
		try {
			// Catch recent news
			news = (News) getSession().createCriteria(News.class).add(Restrictions.eq(newsTableFieldId, news.getNewsId())).uniqueResult();
			
			// Populate archive object
			NewsArchive archive = new NewsArchive();
			archive.setNewsID(news.getNewsId());
			archive.setAuthorID(news.getAuthorId());
			archive.setDate(news.getDate());
			archive.setTitle(news.getTitle());
			archive.setContent(news.getContent());
			
			// Persist archive
			getSession().persist(archive);
			
			// Success
			transaction.commit();
		} catch(Exception e) {
			// Rollback on error
			transaction.rollback();
			throw e;
		}
	}
	
	public String getAuthorName(Integer newsID)
	{
		if(accounts.get(newsID)== null) return "Account Deleted";
		return accounts.get(newsID).getName();
	}
	
	public String getAuthorName(News news)
	{
		return getAuthorName(news.getAuthorId());
	}
}
