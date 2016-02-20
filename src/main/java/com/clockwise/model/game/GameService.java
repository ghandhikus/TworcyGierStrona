package com.clockwise.model.game;

import java.security.AccessControlException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clockwise.exceptions.ParameterTooLongException;
import com.clockwise.model.account.Account;
import com.clockwise.model.game.Game;
import com.clockwise.model.game.GameRepositoryCachedDB;

public @Service interface GameService {
	/**
	 * Adds the game by this account.
	 * @param game created by user. Only title and content is required.
	 * @param account which created the game.
	 * @throws AccessControlException if account has no permission to add game.
	 * @throws ParameterTooLongException if title or description is too long
	 * @return game with set it's table id and authorID
	 */
	public Game addGame(Game game, Account account) throws AccessControlException, ParameterTooLongException;
	public Game createGame(String title, String description, Account account) throws AccessControlException, ParameterTooLongException;
	
	/**
	 * Updates game by this account.
	 * @param game data to update.
	 * @param account which updated the game.
	 * @throws AccessControlException if account has no permission to update game.
	 * @throws ParameterTooLongException if title or description is too long
	 * @return updated game
	 */
	public Game updateGame(Game game, Account account) throws AccessControlException, ParameterTooLongException;
	public Game updateGame(Integer id, String title, String description, Account account) throws AccessControlException, ParameterTooLongException;
	public Game updateGame(Integer id, String title, String description, List<String> newImages, Account account) throws AccessControlException, ParameterTooLongException;
	
	/**
	 * Removes the game by this account.
	 * @param game which are going to be removed.
	 * @param account which removes the game.
	 * @throws AccessControlException if account has no permission to remove game.
	 * @throws ParameterTooLongException if title or description is too long
	 */
	public void removeBy(Game game, Account account) throws AccessControlException, ParameterTooLongException;
	
	/**
	 * Removes the game by this account.
	 * @param gameId of game which are going to be removed.
	 * @param account which removes the game.
	 * @throws AccessControlException if account has no permission to remove game.
	 */
	public void removeBy(Integer gameId, Account account) throws AccessControlException;

	/**
	 * Catches the count of records from database. It is fast if cached version of repository will be used.
	 * @see GameRepositoryCachedDB
	 */
	public Integer getGameCount();
	
	/**
	 * Catches game by it's id.
	 * @param gameId
	 * @return null if no game was found
	 */
	public Game getSpecificByID(Integer gameId);
	
	/**
	 * Catches game created by the given account.
	 * @param account uses id of the user.
	 * @param count of entries for the catch
	 * @param offset from when should the counting start
	 * @return null if no game were found.
	 */
	public List<Game> getUserGames(Account account, Integer count, Integer offset);
	
	/**
	 * Catches all games.
	 * @param count of entries for the catch
	 * @param offset from when should the counting start
	 * @return null if no game were found.
	 */
	public List<Game> getRecentGames(Integer count, Integer offset);
	
	/**
	 * Checks if specified account can edit current game.
	 * @param account
	 * @param id
	 * @throws ParameterTooLongException if game received contains too long parameters
	 * @throws NullPointerException if game requested does not exist
	 * @return false if can't
	 */
	public boolean canEdit(Account account, Integer id) throws NullPointerException, ParameterTooLongException;
	
	public String getAuthorName(Integer gameId);
	public String getAuthorName(Game game);
}
