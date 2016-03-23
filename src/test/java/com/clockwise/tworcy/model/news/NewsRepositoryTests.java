package com.clockwise.tworcy.model.news;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.clockwise.tworcy.model.news.News;
import com.clockwise.tworcy.model.news.NewsRepository;
import com.clockwise.tworcy.model.news.NewsRepositoryCachedDB;

/**
 * Tests {@link NewsRepository}
 * @author Daniel
 */
@WebAppConfiguration // MVC
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring-dispatcher-servlet.xml"})
public class NewsRepositoryTests {
	// Services
	@Autowired NewsRepository newsDB;

	// Cleanup
	List<News> toDelete = new ArrayList<>();
	
	/** Removes all created news by this test. */
	public @After @Test void dispose() {
		for(News news : toDelete)
			try {
				if(news == null) continue;
				
				newsDB.delete(news);
			} catch(Exception e) {
				
			}
	}
	
	/**
	 * Checks if repository can handle common database operations
	 * <a href="https://www.google.com/search?q=CRUD">(CRUD)</a>
	 */
	public @Transactional @Test void insertUpdateSelectDelete()
	{
		System.err.println("LAUNCHING SOMETHING ");
		
		String err = " is badly implemented for "+newsDB.getClass();
		News news = new News();
		news.setAuthorID(0);
		news.setDate(DateTime.now());
		news.setTitle("Title");
		news.setContent("Content");
		toDelete.add(news);
		news = newsDB.insert(news);
		toDelete.add(news);
		
		// Should always be true
		Assert.assertTrue("Insert"+err, news.getTitle().equals("Title"));
		
		// Modify and update the news
		news.setTitle("NewTitle");
		newsDB.update(news);
		toDelete.add(news);
		
		// If cached db updated news class then this shouldn't crash.
		if(newsDB.getClass() == NewsRepositoryCachedDB.class)
			Assert.assertTrue("Update Cache"+err, news.getTitle().equals("NewTitle"));
		
		// Get current version
		news = newsDB.getSpecific(news.getNewsID());
		toDelete.add(news);
		
		Assert.assertNotNull(news);
		
		// Current version should be modified
		Assert.assertTrue("getSpecific"+err, news.getTitle().equals("NewTitle"));

		Assert.assertNotNull(news.getNewsID());
		newsDB.delete(news);
		toDelete.add(news);
		
		news = newsDB.getSpecific(news.getNewsID());
		toDelete.add(news);
		
		// Should be null now
		Assert.assertNull("delete"+err, news);
	}
}
