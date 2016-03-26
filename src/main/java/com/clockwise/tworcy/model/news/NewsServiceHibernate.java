/**
 * 
 */
package com.clockwise.tworcy.model.news;

import java.security.AccessControlException;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clockwise.tworcy.exception.ParameterTooLongException;
import com.clockwise.tworcy.model.account.Account;
import com.clockwise.tworcy.util.AccountPermissions;
import com.mysql.jdbc.Messages;

/**
 * @author Daniel
 */
public @Service("newsService") class NewsServiceHibernate implements NewsService {

	private @Autowired NewsDAO db;
	private @Autowired AccountPermissions permissions;
	
	
	private void prepare(News news, Account account)
	{
		news.setDate(DateTime.now());
		news.setAuthorId(account.getId());
	}

	@Override
	public News createNews(String title, String content, Account account) throws AccessControlException, NullPointerException, ParameterTooLongException {
		News news = new News();
		news.setTitle(title);
		news.setContent(content);
		news.setAuthorId(account.getId());
		
		return addNews(news, account);
	}
	@Override
	public News addNews(News news, Account account) throws AccessControlException, NullPointerException, ParameterTooLongException {
		permissions.checkParameters(news, account);
		permissions.checkAddingNews(news, account);
		prepare(news, account);
		
		return db.insert(news);
	}

	public @Override News updateNews(News news, Account account) throws AccessControlException, NullPointerException, ParameterTooLongException {
		permissions.checkParameters(news, account);
		permissions.checkUpdatingNews(news, account);
		
		db.update(news);
		
		return db.getSpecific(news.getNewsId());
	}

	public @Override News updateNews(Integer id, String title, String content, Account account) throws AccessControlException, NullPointerException, ParameterTooLongException {
		permissions.checkParameter(account);
		News news = getSpecificByID(id);
		permissions.checkParameter(news);
		
		news.setTitle(title);
		news.setContent(content);
		
		return updateNews(news, account);
	}

	@Override
	public void removeBy(News news, Account account) throws AccessControlException, NullPointerException, ParameterTooLongException {
		permissions.checkParameters(news, account);
		permissions.checkRemovingNews(news, account);
		
		db.delete(news);
	}

	@Override
	public Integer getNewsCount() {
		return db.getCount();
	}

	@Override
	public News getSpecificByID(Integer newsID) {
		if(newsID == null) throw new NullPointerException(Messages.getString("News.notSetID"));
		
		return db.getSpecific(newsID);
	}

	@Override
	public List<News> getUserNews(Account account, Integer count, Integer offset) {
		if(count == null) count = 10;
		if(offset == null) offset = 0;
		permissions.checkParameter(account); // checks null and id
		
		return db.getUserNews(account.getId(), count, offset);
	}

	@Override
	public List<News> getRecentNews(Integer count, Integer offset) {
		if(count == null) count = 10;
		if(offset == null) offset = 0;
		
		return db.getRecent(count, offset);
	}

	@Override
	public boolean canEdit(Account account, Integer id) throws NullPointerException, ParameterTooLongException {
		permissions.checkParameter(account); // checks null and id
		News news = getSpecificByID(id);
		permissions.checkParameter(news);
		return !(!permissions.isAuthor(news, account) && !permissions.canUpdateNews(account));
	}

	public @Override String getAuthorName(Integer newsID)
	{
		return db.getAuthorName(newsID);
	}
	
	public @Override String getAuthorName(News news)
	{
		return db.getAuthorName(news);
	}
}
