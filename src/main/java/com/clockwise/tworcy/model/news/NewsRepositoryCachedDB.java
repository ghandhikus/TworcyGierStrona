package com.clockwise.tworcy.model.news;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.clockwise.tworcy.model.account.AccountService;

/**
 * Implementation of {@link NewsRepository} that uses @{@link Autowired} database and caches results.
 * <br>
 * By using this class you agree that nothing else should modify database results for `news` table.
 * <br><br>
 * Parameters are bullet proof, they don't throw exceptions when some of the parameters is null.
 * <br>
 * Please check the source to check how parameters will react for nulls.
 * <br>
 * News parameters are not html escaped. It is just plain object database operation.
 * @author Daniel
 */
@Repository class NewsRepositoryCachedDB implements NewsRepository {
	/** Database object, is automatically injected by Spring */
	private @Autowired JdbcTemplate db;
	private @Autowired AccountService accounts;

	/** Cached value, specifies number of records in database. */
	private volatile Integer newsCount = null;
	/** List cache. Requires {@link synchronize} block to ensure that it remains thread safe. */
	private List<News> cache = Collections.synchronizedList(new ArrayList<News>());
	/** KeyHolder to catch id from insert statements. */
	private KeyHolder keyHolder = new GeneratedKeyHolder();

	/**
	 * Is called by Spring after it sets @{@link Autowired} for database.
	 * <br>
	 * Prepares cache.
	 */
	public @PostConstruct void init() {
		if (newsCount == null)
			forceUpdateCount();
	}

	/**
	 * Forces counting of news from the database.
	 */
	public void forceUpdateCount() {
		// Catch count of news.
		String sql = "SELECT COUNT(*) FROM news";
		try {
			newsCount = db.queryForObject(sql, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			newsCount = 0;
		}
	}

	
	public @Override News insert(News news) {
		// Check news parameters
		if (news == null) return null;
		if (news.getAuthorID() == null) return null;
		if (news.getDate() == null) return null;
		if (news.getTitle() == null) return null;
		if (news.getContent() == null) return null;
		
		// Insert statement for news
		String sql = "INSERT INTO news (authorID, date, title, content) VALUES (?, ?, ?, ?)";

		// Convert news time to sql timestamp
		Timestamp timeStamp = new Timestamp(news.getDate().getMillis());

		// Thread safety for keyholder
		synchronized(keyHolder)
		{
			// Prepared statement for catching the id
			db.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
					// News table values
					// Ordered from 1!
					ps.setInt(1, news.getAuthorID());
					ps.setTimestamp(2, timeStamp);
					ps.setString(3, news.getTitle());
					ps.setString(4, news.getContent());
					return ps;
				}
			}, keyHolder);
	
			// Get id from db
			news.setNewsID(((Long) keyHolder.getKey()).intValue());
		}
		
		// Cache results
		synchronized(cache) {
			cache.add(news);
		}
		newsCount++;
		
		// Return news with changed id
		return news;
	}

	public @Override void update(News news) {
		if (news == null) return;
		if (news.getAuthorID() == null) return;
		if (news.getDate() == null) return;
		if (news.getTitle() == null) news.setTitle("null");		// Failsafety
		if (news.getContent() == null) news.setContent("null"); // Failsafety

		// Joda date to sql timestamp
		Timestamp timeStamp = new Timestamp(news.getDate().getMillis());
		
		// Update cache
		synchronized(cache)
		{
			for(News cur : cache)
				if(cur.getNewsID() == news.getNewsID())
				{
					// Do nothing if it is the same object
					if(cur == news) break;
					
					// Set data to cache object
					cur.setAuthorID(news.getAuthorID());
					cur.setDate(news.getDate());
					cur.setTitle(news.getTitle());
					cur.setContent(news.getContent());
					// Break iteration
					break;
				}
		}
		// Update the news
		String sql = "UPDATE news SET authorID=?, date=?, title=?, content=? WHERE id=? LIMIT 1";
		//int rowsAffected = 
		db.update(sql, news.getAuthorID(), timeStamp, news.getTitle(), news.getContent(), news.getNewsID());
	}

	public @Override void delete(News news) {
		if (news == null)
			return;
		if (news.getNewsID() == null)
			return;

		delete(news.getNewsID());
	}

	public @Override void delete(Integer id) {
		if (id == null)
			return;
		
		synchronized(cache)
		{
			for(News cur : cache)
				if(cur.getNewsID() == id)
				{
					cache.remove(cur);
					break;
				}
		}
		
		// Delete and substract the news count from cache.
		String sql = "DELETE FROM news WHERE id = ? LIMIT 1";
		int rowsAffected = db.update(sql, id);
		newsCount -= rowsAffected;
	}

	public @Override int getCount() {
		if (newsCount == null)
			throw new NullPointerException("Database is badly configured. newsCount was not loaded.");
		return newsCount;
	}

	public @Override News getSpecific(Integer newsID) {
		if (newsID == null)
			return null;

		// There is no news in db so no need to get anything
		if (newsCount == 0) return null;

		// Search through cache
		synchronized(cache) {
			for(News news : cache) if(news.getNewsID().equals(newsID)) return news;
		}
		// Try to catch the specific or return null.
		try {
			String sql = "SELECT * FROM news WHERE id = ? LIMIT 1";
			return db.queryForObject(sql, new Object[] { newsID }, new NewsRepositoryDBMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public @Override List<News> getUserNews(Integer accountID, Integer count, Integer offset) {
		if (accountID == null) return null;
		if (count == null) count = 10;
		if (offset == null) offset = 0;
		if (count > 1000) count = 1000;
		// Make sure that offset won't be higher than newsCount
		if (offset > newsCount) offset = newsCount - 1;
		// There is no news in db so no need to get anything
		if (newsCount == 0) return null;

		String sql = "SELECT * FROM news WHERE authorID = ? ORDER BY date, id DESC LIMIT ? OFFSET ?";

		@SuppressWarnings("unchecked")
		List<News> ret = (List<News>) db.queryForObject(sql, new NewsRepositoryDBMapper(),
				new Object[]{accountID,count,offset});
		
		if(ret.size()==0)
			return null;
		else
			return ret;
	}

	public @Override List<News> getRecent(Integer count, Integer offset) {
		if (count == null) count = 10;
		if (offset == null) offset = 0;
		if (count > 1000) count = 1000;
		// Make sure that offset won't be higher than newsCount
		if (offset > newsCount) offset = newsCount - 1;
		// There is no news in db so no need to get anything
		if (newsCount == 0) return null;

		// Cache won't work here, as it supports offsets
		
		// Get news
		String sql = "SELECT * FROM news ORDER BY date, id DESC LIMIT ? OFFSET ?";
		List<News> ret = (List<News>) db.query(sql, new NewsRepositoryDBMapper(),
				new Object[]{count, offset});
		
		if(ret.size()==0)
			return null;
		else
			return ret;
	}

	public @Override void archive(News news) {
		if (news == null) return;
		// Archive current news
		archive(news.getNewsID());
	}

	public @Override void archive(Integer id) {
		if(id == null) return;
		// Archive current news
		String sql = "INSERT INTO news_archive(newsID, authorID, date, title, content)"+
				"SELECT id as newsID, authorID, date, title, content FROM news WHERE id = ? LIMIT 1";
		db.update(sql, id);
	}
	
	public String getAuthorName(Integer newsID)
	{
		if(accounts.get(newsID)== null) return "Account Deleted";
		return accounts.get(newsID).getName();
	}
	
	public String getAuthorName(News news)
	{
		return getAuthorName(news.getAuthorID());
	}
}
