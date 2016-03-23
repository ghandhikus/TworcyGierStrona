package com.clockwise.tworcy.model.news;

import static org.junit.Assert.*;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.clockwise.tworcy.model.account.Access;
import com.clockwise.tworcy.model.account.Account;
import com.clockwise.tworcy.model.account.AccountService;
import com.clockwise.tworcy.model.news.News;
import com.clockwise.tworcy.model.news.NewsRepository;
import com.clockwise.tworcy.model.news.NewsService;

/**
 * Tests {@link NewsService}
 * @author Daniel
 */
@WebAppConfiguration // MVC
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring-dispatcher-servlet.xml"})
public class NewsServiceTests {
	// Services
	@Autowired NewsService newsService;
	@Autowired AccountService accounts;
	
	// Database for making sure that data will be deleted
	@Autowired NewsRepository db;
	
	// Cleanup
	List<News> toDelete = new ArrayList<>();
	Integer cleanupTestID = null;
	
	// Accounts for testing
	Account admin, account;
	
	/** Prepares this class for tests */
	public @Before @Test void init()
	{
		// TODO: create accounts just for testing.
		// Catch admin account
		admin = accounts.get("Ghandhikus");
		// Catch normal account
		account = accounts.get("Dummy");

		// Create news by him
		News news = new News();
		news.setTitle("NewsServiceTests");
		news.setContent("Some random content");
		toDelete.add(news);
		news = newsService.addNews(news, admin);
		Assert.assertNotNull("Test was not created for cleanup test.", news);
		
		cleanupTestID = news.getNewsID();
	}
	
	/** Cleans up after this class tests. Removes data from database. */
	public @After @Test void dispose()
	{
		for(News news : toDelete)
			try {
				db.delete(news);
			} catch(NullPointerException e) {
				// Catch common for remove exceptions
			}
		
		News news = newsService.getSpecificByID(cleanupTestID);
		Assert.assertNull("Cleanup was not succesful.", news);
	}
	
	/** Tests all admin specific behavior. */
	public @Transactional @Test void adminTests()
	{
		String err = " is badly implemented for "+newsService.getClass();
		
		// Checks if account has permissions
		Assert.assertTrue("Specified admin account for tests is not a moderator or higher!", admin.getAccess() >= Access.MODERATOR.getAccess());
		
		// Create news by him
		News news = new News();
		news.setTitle("NewsServiceTests");
		news.setContent("Some random content");
		toDelete.add(news);
		toDelete.add(news = newsService.addNews(news, admin));
		Assert.assertNotNull("addNews returned null, addNews"+err, news);
		
		// Update
		news.setContent("NewContent!");
		toDelete.add(news = newsService.updateNews(news, admin));
		Assert.assertNotNull("updateNews returned null, updateNews"+err, news);
		
		// Load the news
		toDelete.add(news = newsService.getSpecificByID(news.getNewsID()));
		Assert.assertNotNull("Can't load the news previously updated, getSpecificByID"+err, news);
		Assert.assertTrue("content does not equal the changes of the previous update, getSpecificByID"+err, news.getContent().equals("NewContent!"));
		
		// Delete the news
		newsService.removeBy(news, admin);
		toDelete.add(news = newsService.getSpecificByID(news.getNewsID()));
		Assert.assertNull("Can't remove the news by admin, removeBy"+err, news);
	}
	
	/** Tests if normal user can hack the news service. */
	public @Transactional @Test void normalHackTests()
	{
		String err = " is badly implemented for "+newsService.getClass();
		
		// Checks if account has permissions
		Assert.assertTrue("Specified account for tests is not a normal user!", account.getAccess() == Access.NORMAL.getAccess());
		
		// Create news by him
		News news = new News();
		news.setTitle("NewsServiceTests");
		news.setContent("Some random content");
		try {
			news = newsService.addNews(news, account);
			// this shouldnt fire
			toDelete.add(news);
		} catch (AccessControlException e) {
			news = null;
		}
		Assert.assertNull("addNews did not return null for normal user, addNews"+err, news);
		
		// Admin news
		News adminNews = newsService.createNews("test", "test", admin);
		toDelete.add(adminNews);

		// Tries to update with account that has no access to update.
		// In real world nothing should have access to setting these variables.
		// As the accessor is default and can only be accessed by the model and tests.
		adminNews.setTitle("FakeUpdateApi");
		adminNews.setContent("Try to hack me!");
		
		try {
			news = newsService.updateNews(adminNews, account);
			// this should'nt fire
			toDelete.add(news);
		} catch (AccessControlException e) {
			news = null;
		}
		Assert.assertNull("updateNews returned did not return null for normal user, updateNews"+err, news);
	}
	
	/** Tests specific to normal user behavior. */
	public @Transactional @Test void normalTests()
	{
		String err = " is badly implemented for "+newsService.getClass();
		
		// Checks if account has permissions
		Assert.assertTrue("Specified normal account for tests is not a normal user!"+err, account.getAccess() == Access.NORMAL.getAccess());
		Assert.assertTrue("Specified admin account for tests is a normal user!"+err, admin.getAccess() >= Access.MODERATOR.getAccess());
		
		// Create any news to see
		News adminNews = newsService.createNews("test", "test", admin);
		toDelete.add(adminNews);
		Integer id = adminNews.getNewsID();
		
		// Load recent news
		List<News> recentNews = newsService.getRecentNews(10, 0);
		boolean contains = false;
		
		for(News news : recentNews)
			if(news.getNewsID().equals(id))
				contains = true;
		
		assertTrue("Users can't load recent news.", contains);
		
		// Checking news parameters for recent news
		for(News news : recentNews)
			if(news.getNewsID().equals(id))
			{
				String title = news.getTitle();
				String content = news.getContent();
				String creatorName = newsService.getAuthorName(news);
	
				assertTrue("News parameter is not as it should be. Parameter : authorID is("+news.getAuthorID()+") shouldBe("+admin.getId()+")", news.getAuthorID().equals(admin.getId()));
				assertTrue("News parameter is not as it should be. Parameter : title is("+title+") shouldBe(test)", title.equals("test"));
				assertTrue("News parameter is not as it should be. Parameter : content is("+content+") shouldBe(test)", content.equals("test"));
				assertTrue("NewsService can't catch the correct name. is("+creatorName+") shouldBe("+admin.getName()+")", creatorName.equals(admin.getName()));
			}
	}
	
	public @Transactional @Test void checks_If_News_Can_Break_When_There_Is_No_ID_in_DB()
	{
		// Create news by admin
		News n = newsService.createNews("test", "test", admin);
		toDelete.add(n);
		// Get id
		Integer id = n.getNewsID();
		// Remove news
		newsService.removeBy(id, admin);
		
		News test = newsService.getSpecificByID(id);
		
		assertNull("NewsService is broken. News still persists after deleting it. Test detected non-null News when using deleted news id.", test);
	}
}
