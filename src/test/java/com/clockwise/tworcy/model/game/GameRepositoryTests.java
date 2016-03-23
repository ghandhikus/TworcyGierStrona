package com.clockwise.tworcy.model.game;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

import com.clockwise.tworcy.model.account.AccountService;
import com.clockwise.tworcy.model.game.Game;
import com.clockwise.tworcy.model.game.GameRepository;
/**
 * Tests {@link GameRepository}
 * @author Daniel
 */
@WebAppConfiguration // MVC
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring-dispatcher-servlet.xml"})
public class GameRepositoryTests {

	// Services
	@Autowired AccountService accounts;

	// Database to test
	@Autowired GameRepository db;
	
	// Cleanup
	List<Game> toDelete = new ArrayList<>();
	Integer cleanupTestID = null;

	/** Creates Game object and adds it to cleanup deletion in case of test fails. */
	Game createGameObject() { 
		Game game = new Game();
		game.setAuthorId(accounts.get("Ghandhikus").getId());
		game.setTitle("NewsServiceTests");
		game.setDescription("Some random content");
		toDelete.add(game);
		return game;
	}
	
	/** Prepares the class for testing */
	public @Before @Test void init() {
		assertNotNull(db);

		// Create news by him
		Game game = db.insert(createGameObject());
		assertNotNull("Init test for GameRepository returned null after insert statement.", game);
		
		cleanupTestID = game.getGameId();
	}
	
	/** Removes entries created by this test. Can fail if {@link GameRepository#delete} is badly written and does not remove the entries from the database. */
	public @After @Test void dispose() {
		// Iterate through objects to delete from db
		for(Game game : toDelete)
			try {
				db.delete(game);
			} catch(Exception e) {
				
			}
		
		// Test if test can dispose it's added data when some of them fail to do so
		Game game = db.getSpecific(cleanupTestID);
		Assert.assertNull("Cleanup was not succesful.", game);
	}

	/**
	 * Checks if repository can handle common database operations
	 * <a href="https://www.google.com/search?q=CRUD">(CRUD)</a>
	 */
	public @Transactional @Test void insertUpdateSelectDelete() {
		/** Create */
		
		// Allocating object
		Game game = createGameObject();
		
		// Inserting
		game = db.insert(game);
		assertNotNull("Cannot insert game to database. Returned null.", game);
		
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
		Integer deleteID = game.getGameId();
		db.delete(game);
		
		// Check if purposely deleted object still exist
		game = db.getSpecific(deleteID);
		assertNull("Cannot remove game from database. Returned value is not null", game);
	}
}
