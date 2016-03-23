package com.clockwise.tworcy.model.news;

import java.security.AccessControlException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clockwise.tworcy.exception.ParameterTooLongException;
import com.clockwise.tworcy.model.account.Account;

/**
 * Specifies medium between database and user.
 * @author Daniel
 */
public @Service interface NewsService {
	/**
	 * Adds the news by this account.
	 * @param news created by user. Only title and content is required.
	 * @param account which created the news.
	 * @throws AccessControlException if account has no permission to add news.
	 * @return news with set it's table id and authorID
	 * @throws ParameterTooLongException 
	 * @throws NullPointerException 
	 */
	public News addNews(News news, Account account) throws AccessControlException, NullPointerException, ParameterTooLongException;
	public News createNews(String title, String content, Account account) throws AccessControlException, NullPointerException, ParameterTooLongException;
	
	/**
	 * Updates news by this account.
	 * @param news data to update.
	 * @param account which updated the news.
	 * @throws AccessControlException if account has no permission to update news.
	 * @return updated news
	 * @throws ParameterTooLongException 
	 * @throws NullPointerException 
	 */
	public News updateNews(News news, Account account) throws AccessControlException, NullPointerException, ParameterTooLongException;
	public News updateNews(Integer id, String title, String content, Account account) throws AccessControlException, NullPointerException, ParameterTooLongException;
	
	/**
	 * Removes the news by this account.
	 * @param news which are going to be removed.
	 * @param account which removes the news.
	 * @throws AccessControlException if account has no permission to remove news.
	 * @throws ParameterTooLongException 
	 * @throws NullPointerException 
	 */
	public void removeBy(News news, Account account) throws AccessControlException, NullPointerException, ParameterTooLongException;
	
	/**
	 * Removes the news by this account.
	 * @param newsID of news which are going to be removed.
	 * @param account which removes the news.
	 * @throws AccessControlException if account has no permission to remove news.
	 */
	public void removeBy(Integer newsID, Account account) throws AccessControlException;

	/**
	 * Catches the count of records from database. It is fast if cached version of repository will be used.
	 * @see NewsRepositoryCachedDB
	 */
	public Integer getNewsCount();
	
	/**
	 * Catches news by it's id.
	 * @param newsID
	 * @return null if no news was not found
	 */
	public News getSpecificByID(Integer newsID);
	
	/**
	 * Catches news created by the given account.
	 * @param account uses id of the user.
	 * @param count uses id of the user.
	 * @param offset uses id of the user.
	 * @return null if no news were found.
	 */
	public List<News> getUserNews(Account account, Integer count, Integer offset);
	
	/**
	 * Catches all news.
	 * @param count
	 * @param offset
	 * @return null if no news were found.
	 */
	public List<News> getRecentNews(Integer count, Integer offset);
	
	/**
	 * Checks if specified account can edit current news.
	 * @param account
	 * @param id
	 * @return false if can't
	 * @throws ParameterTooLongException 
	 * @throws NullPointerException 
	 */
	public boolean canEdit(Account account, Integer id) throws NullPointerException, ParameterTooLongException;
	
	public String getAuthorName(Integer newsID);
	public String getAuthorName(News news);
}
