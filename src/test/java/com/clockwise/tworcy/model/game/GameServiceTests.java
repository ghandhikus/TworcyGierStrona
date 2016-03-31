package com.clockwise.tworcy.model.game;

import static org.junit.Assert.*;

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
 * Tests {@link GameService}
 * @author Daniel
 */
@WebAppConfiguration // MVC
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring-dispatcher-servlet.xml"})
@Transactional(propagation=Propagation.REQUIRED)
public class GameServiceTests implements AccountInject {
	// Services
	@Autowired GameService gameService;
	@Autowired AccountService accounts;
	
	// Database to make sure that data will be deleted
	@Autowired GameDAO db;

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
		return new Access[]{Access.NORMAL, Access.HEADADMIN};
	}

	@Override
	public void inject(Account[] account) {
		this.account = account[0];
		this.admin = account[1];
	}
	/////////////////////////////////////////////////////////////////////
	
	/** Creates Game object and adds it to cleanup deletion in case test fails. */
	Game createGameObject()
	{
		Game game = new Game();
		game.setTitle("GameServiceTests");
		game.setDescription("Some random content");
		return game;
	}


	/** Checks behavior for admin accounts. Creates a game, updates it, loads it and removes. */
	@Rollback(true)
	public @Test void adminTests() {
		String err = " is badly implemented for "+gameService.getClass();

		checkPermissions();
		
		// Create game by him
		Game game = createGameObject();
		game = gameService.addGame(game, admin);
		Assert.assertNotNull("addGame returned null, in addGame"+err, game);
		// Update
		game.setDescription("NewContent!");
		game = gameService.updateGame(game, admin);
		Assert.assertNotNull("updateGame returned null, in updateGame"+err, game);
		
		// Load the game
		game = gameService.getSpecificByID(game.getGameId());
		Assert.assertNotNull("getSpecificByID returned null, in getSpecificByID"+err, game);
		Assert.assertTrue("content does not equal the changes of the previous update, in getSpecificByID"+err, game.getDescription().equals("NewContent!"));

		// Delete the game
		gameService.removeBy(game, admin);
		game = gameService.getSpecificByID(game.getGameId());
		Assert.assertNull("removeBy didn't removed the game, in removeBy"+err, game);
	}
	
	/** Attempts to hack a game created by different user with normal account. */
	@Rollback(true)
	public @Test void normalHackTests()
	{
		String err = " is badly implemented for "+gameService.getClass();

		checkPermissions();
		
		// Creating game by admin
		Game game = createGameObject();
		try {
			game = gameService.addGame(game, admin);
		} catch (AccessControlException e) {
			game = null;
		}
		Assert.assertNotNull("addGame returned null for admin user, in addGame"+err, game);
		
		// Attempt to update
		try {
			game = gameService.updateGame(game, account);
			// this shouldnt fire
			Assert.assertTrue("This shouldn't fire due to exception thrown above", false);
		} catch (AccessControlException e) {
			game = null;
		}
		Assert.assertNull("updateGame did not return null for normal user, in updateGame"+err, game);
	}
	
	/** Tests the case for ordinary user that will just browse the website without doing anything. */
	@Rollback(true)
	public @Test void lurkerCase()
	{
		String err = " is badly implemented for "+gameService.getClass();
		
		checkPermissions();

		// Adding games for testing
		Game game1 = createGameObject();
		game1.setTitle("#2!4#");
		game1 = gameService.addGame(game1, admin);
		Game game2 = createGameObject();
		game2.setTitle("&!()");
		game2 = gameService.addGame(game2, admin);
		
		// Browsing games
		List<Game> recentGames = gameService.getRecentGames(10, 0);

		boolean containsGame1 = false;
		boolean containsGame2 = false;
		
		for(Game game : recentGames)
		{
			if(game.getTitle().equals(game1.getTitle())) containsGame1 = true;
			if(game.getTitle().equals(game2.getTitle())) containsGame2 = true;
		}

		assertTrue("One or more of games, were not loaded, in getRecentGame"+err, containsGame1);
		assertTrue("One or more of games, were not loaded, in getRecentGame"+err, containsGame2);
		
		// Getting into the game (single load)
		assertNotNull("Can't load specific game by id, in getSpecificByID"+err, gameService.getSpecificByID(game1.getGameId()));
		
		// Browsing user game
		List<Game> userGames = gameService.getUserGames(admin, 10, 0);
		
		containsGame1 = false;
		containsGame2 = false;
		
		// Check if userGames contain added games by him.
		for(Game game : userGames)
		{
			if(game.getTitle().equals(game1.getTitle())) containsGame1 = true;
			if(game.getTitle().equals(game2.getTitle())) containsGame2 = true;
		}

		assertTrue("One or more of games, for specific user, were not loaded, in getUserGames"+err, containsGame1);
		assertTrue("One or more of games, for specific user, were not loaded, in getUserGames"+err, containsGame2);
	}
	
	/** Test the case of creator. Adds, updates and removes the game by normal account. */
	@Rollback(true)
	public @Test void creatorCase()
	{
		String err = " is badly implemented for "+gameService.getClass();
		
		checkPermissions();
		
		// Adding
		Game game1 = createGameObject();
		game1.setTitle("#2!4#");
		game1 = gameService.addGame(game1, account);
		assertNotNull("Creator can't add games, in addGame"+err, game1);
		
		// Updating
		String title = "!@$!!@$";
		game1.setTitle(title);
		game1 = gameService.updateGame(game1, account);
		assertTrue("Creator can't update game created by him, in updateGame"+err, game1.getTitle().equals(title));
		
		// Removing
		Integer id = game1.getGameId();
		gameService.removeBy(game1, account);
		assertNull("Creator can't remove game created by him, in removeBy"+err, gameService.getSpecificByID(id));
	}
	
	/** Attempts to moderate the game with moderator account. Corrects the game of different normal user and removes it.*/
	@Rollback(true)
	public @Test void moderatorCase()
	{
		String err = " is badly implemented for "+gameService.getClass();
		
		checkPermissions();
		
		// Creating game by someone else
		Game createdGame = createGameObject();
		createdGame = gameService.addGame(createdGame, account);
		Integer id = createdGame.getGameId();

		// Correcting
		String correctTitle = "#2!4#";
		Game toCorrect = gameService.getSpecificByID(id);
		toCorrect.setTitle(correctTitle);
		toCorrect = gameService.updateGame(toCorrect, admin);
		assertTrue("Moderator can't update someone's game, in updateGame"+err, toCorrect.getTitle().equals(correctTitle));
		
		// Archive
		// TODO: add this when archive will be supported better

		// Removing
		gameService.removeBy(createdGame, admin);
		Game removed = gameService.getSpecificByID(id);
		assertNull("Moderator can't delete someone's game, in removeBy"+err, removed);
	}
}
