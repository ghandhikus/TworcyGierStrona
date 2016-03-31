package com.clockwise.tworcy.model.game;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.clockwise.tworcy.model.account.Access;
import com.clockwise.tworcy.model.account.Account;
import com.clockwise.tworcy.model.account.AccountInject;
import com.clockwise.tworcy.model.account.AccountService;
import com.clockwise.tworcy.model.account.DummyAccountInjector;
/**
 * Tests {@link GameDAO}
 * @author Daniel
 */
@WebAppConfiguration // MVC
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring-dispatcher-servlet.xml"})
@Transactional
public class GameDAOTests implements AccountInject {

	// Services
	@Autowired AccountService accounts;

	// Database to test
	@Autowired GameDAO db;
	
	Account admin;
	
	/////////////////////////////////////////////////////////////////////
	// Account injection
	@Autowired DummyAccountInjector injector;

	/** Checks if account has permissions */
	private void checkPermissions() {
		Assert.assertTrue("Specified admin account for tests is a normal user!"+admin.toString(), admin.getAccess() >= Access.MODERATOR.getAccess());
	}
	
	public @Before @Test void testAccountInjector(){
		injector.inject(this);
		checkPermissions();
	}
	
	@Override
	public Access[] needs() {
		return new Access[] { Access.HEADADMIN };
	}
	
	@Override
	public void inject(Account[] account) {
		this.admin = account[0];
	}
	/////////////////////////////////////////////////////////////////////


	/** Creates Game object and adds it to cleanup deletion in case of test fails. */
	Game createGameObject() { 
		Game game = new Game();
		game.setAuthorId(accounts.get("Ghandhikus").getId());
		game.setTitle("NewsServiceTests");
		game.setDescription("Some random content");
		game.setDateAdded(DateTime.now());
		return game;
	}

	/**
	 * Checks if repository can handle common database operations
	 * <a href="https://www.google.com/search?q=CRUD">(CRUD)</a>
	 */
	@Rollback(true)
	public @Test void insertUpdateSelectDelete() {
		/** Create */
		
		// Allocating object
		Game game = createGameObject();
		
		// Inserting
		game = db.insert(game);
		assertNotNull("Cannot insert game to database. Returned null.", game);
		
		// Checking if game id has been updated
		assertTrue("Game id has not been updated after the insertion.", game.getGameId()!=0);
		
		/** Read */
		// Reading from db
		game = db.getSpecific(game.getGameId());
		assertNotNull("Cannot read game from database. Returned null.", game);
		
		/** Update */
		String updateStr = "UpdatedTitle";
		game.setTitle(updateStr);
		game = db.update(game);
		
		assertTrue("Cannot update game in database. The result is not the specified one",
				(game.getTitle().equals(updateStr)));
		
		/** Delete */
		// Catch database ID and delete the game
		int deleteID = game.getGameId();
		db.delete(game);
		
		// Check if purposely deleted object still exist
		game = db.getSpecific(deleteID);
		assertNull("Cannot remove game from database. Returned value is not null", game);
	}
}
