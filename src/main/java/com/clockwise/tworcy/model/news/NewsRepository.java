package com.clockwise.tworcy.model.news;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * Specifies basic operations on `news` table.
 * <br>
 * News parameters are not html escaped. It is just plain object database operation.
 * <br>
 * It should be managed by service which checks for modification access.
 * @author Daniel
 */
@Repository interface NewsRepository {
	/** Inserts news object to database, sets it's database id and returns it. */
	public News insert(News news);
	/** Updates all variables except the id which is used to direct the changes to correct news. */
	public void update(News news);
	/** Removes news from database by it's id */
	public void delete(News news);
	/** Removes news from database by it's id */
	public void delete(Integer id);
	/** Copies the news from `news` table to `news_archive` table. Uses id */
	public void archive(News news);
	/** Copies the news from `news` table to `news_archive` table. */
	public void archive(Integer id);

	/** @return number of records in `news` table */
	public int getCount();
	/** Catches the news by specified id. */
	public News getSpecific(Integer newsID);
	/** Catches all news created by account id.
	 * @param accountID - the account id to determine which news were created by the account.
	 * @param count - number of expected/max results. Max value of it is 1000
	 * @param offset - specifies after which entry should the counting begin
	 * @return can return null if list is empty
	 */
	public List<News> getUserNews(Integer accountID, Integer count, Integer offset);
	/**
	 * Catches all news based on count and offset given.
	 * @param count - number of expected/max results. Max value of it is 1000
	 * @param offset - specifies after which entry should the counting begin
	 * @return can return null if list is empty
	 */
	public List<News> getRecent(Integer count, Integer offset);

	public String getAuthorName(Integer newsID);
	public String getAuthorName(News news);
}
