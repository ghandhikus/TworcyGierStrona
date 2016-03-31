package com.clockwise.tworcy.model.news;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.security.AccessControlException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.clockwise.tworcy.model.account.Access;
import com.clockwise.tworcy.model.account.Account;
import com.clockwise.tworcy.model.account.AccountInject;
import com.clockwise.tworcy.model.account.AccountService;
import com.clockwise.tworcy.model.account.DummyAccountInjector;

/**
 * Tests {@link NewsService}
 * @author Daniel
 */
@WebAppConfiguration // MVC
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring-dispatcher-servlet.xml"})
@Transactional(propagation=Propagation.REQUIRED)
public class NewsServiceTests implements AccountInject {
	// Services
	@Autowired NewsService newsService;
	@Autowired AccountService accounts;
	
	// Database for making sure that data will be deleted
	@Autowired NewsDAO db;


	/** Checks if account has permissions */
	private void checkPermissions() {
		Assert.assertTrue("Specified normal account for tests is not a normal user! "+account.toString(), account.getAccess() == Access.NORMAL.getAccess());
		Assert.assertTrue("Specified admin account for tests is a normal user!"+admin.toString(), admin.getAccess() >= Access.MODERATOR.getAccess());
	}
	/////////////////////////////////////////////////////////////////////
	// Account injection
	@Autowired DummyAccountInjector injector;
	Account account, admin;
	
	public @Before @Test void testAccountInjector(){
		injector.inject(this);
		checkPermissions();
	}

	@Override
	public Access[] needs() {
		return new Access[] { Access.NORMAL, Access.HEADADMIN };
	}

	@Override
	public void inject(Account[] account) {
		this.account = account[0];
		this.admin = account[1];
	}
	/////////////////////////////////////////////////////////////////////
	
	/** Prepares this class for tests */
	@Rollback(true)
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
		news = newsService.addNews(news, admin);
		Assert.assertNotNull("Test was not created for cleanup test.", news);
	}

	/** Tests all admin specific behavior. */
	@Rollback(true)
	public @Test void adminTests()
	{
		String err = " is badly implemented for "+newsService.getClass();
		
		// Checks if account has permissions
		Assert.assertTrue("Specified admin account for tests is not a moderator or higher!", admin.getAccess() >= Access.MODERATOR.getAccess());
		
		// Create news by him
		News news = new News();
		news.setTitle("NewsServiceTests");
		news.setContent("Some random content");
		news = newsService.addNews(news, admin);
		Assert.assertNotNull("addNews returned null, addNews"+err, news);
		
		// Update
		news.setContent("NewContent!");
		news = newsService.updateNews(news, admin);
		Assert.assertNotNull("updateNews returned null, updateNews"+err, news);
		
		// Load the news
		news = newsService.getSpecificByID(news.getNewsId());
		Assert.assertNotNull("Can't load the news previously updated, getSpecificByID"+err, news);
		Assert.assertTrue("content does not equal the changes of the previous update, getSpecificByID"+err, news.getContent().equals("NewContent!"));
		
		// Delete the news
		newsService.removeBy(news, admin);
		news = newsService.getSpecificByID(news.getNewsId());
		Assert.assertNull("Can't remove the news by admin, removeBy"+err, news);
	}
	
	/** Tests if normal user can hack the news service. */
	@Rollback(true)
	public @Test void normalHackTests()
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
		} catch (AccessControlException e) {
			news = null;
		}
		Assert.assertNull("addNews did not return null for normal user, addNews"+err, news);
		
		// Admin news
		News adminNews = newsService.createNews("test", "test", admin);

		// Tries to update with account that has no access to update.
		// In real world nothing should have access to setting these variables.
		// As the accessor is default and can only be accessed by the model and tests.
		adminNews.setTitle("FakeUpdateApi");
		adminNews.setContent("Try to hack me!");
		
		try {
			news = newsService.updateNews(adminNews, account);
			// this should'nt fire
		} catch (AccessControlException e) {
			news = null;
		}
		Assert.assertNull("updateNews returned did not return null for normal user, updateNews"+err, news);
	}
	
	/** Tests specific to normal user behavior. */
	@Rollback(true)
	public @Test void normalTests()
	{
		String err = " is badly implemented for "+newsService.getClass();
		
		// Checks if account has permissions
		Assert.assertTrue("Specified normal account for tests is not a normal user!"+err, account.getAccess() == Access.NORMAL.getAccess());
		Assert.assertTrue("Specified admin account for tests is a normal user!"+err, admin.getAccess() >= Access.MODERATOR.getAccess());
		
		// Create any news to see
		News adminNews = newsService.createNews("test", "test", admin);
		Integer id = adminNews.getNewsId();
		
		// Load recent news
		List<News> recentNews = newsService.getRecentNews(10, 0);
		boolean contains = false;
		
		for(News news : recentNews)
			if(news.getNewsId() == id)
				contains = true;
		
		assertTrue("Users can't load recent news.", contains);
		
		// Checking news parameters for recent news
		for(News news : recentNews)
			if(news.getNewsId() == id)
			{
				String title = news.getTitle();
				String content = news.getContent();
				String creatorName = newsService.getAuthorName(news);
	
				assertTrue("News parameter is not as it should be. Parameter : authorID is("+news.getAuthorId()+") shouldBe("+admin.getId()+")", news.getAuthorId() == admin.getId());
				assertTrue("News parameter is not as it should be. Parameter : title is("+title+") shouldBe(test)", title.equals("test"));
				assertTrue("News parameter is not as it should be. Parameter : content is("+content+") shouldBe(test)", content.equals("test"));
				assertTrue("NewsService can't catch the correct name. is("+creatorName+") shouldBe("+admin.getName()+")", creatorName.equals(admin.getName()));
			}
	}
	
	@Rollback(true)
	public @Test void checks_If_News_Can_Break_When_There_Is_No_ID_in_DB()
	{
		// Create news by admin
		News n = newsService.createNews("test", "test", admin);
		// Get id
		int id = n.getNewsId();
		// Remove news
		newsService.removeBy(n, admin);
		
		News test = newsService.getSpecificByID(id);
		
		assertNull("NewsService is broken. News still persists after deleting it. Test detected non-null News when using deleted news id.", test);
	}
}
