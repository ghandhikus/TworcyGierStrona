package com.clockwise.tworcy.model.game;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository interface GameRepository {
	public Game insert(Game game);
	public Game update(Game game);
	public void delete(Game game);
	public void archive(Game game);

	/** @return number of games in the database */
	public int getCount();
	/** @return number of games in the database by the given user */
	public int getUserGameCount(Integer authorId);
	
	/** Catches the game by specified id. */
	public Game getSpecific(Integer gameId);
	/** Catches all games created by account id.
	 * @param accountID - the account id to determine which ones were created by the account.
	 * @param count - number of expected/max results. Max value of it is 1000
	 * @param offset - specifies after which entry should the counting begin
	 * @return can return null if list is empty
	 */
	public List<Game> getUserGames(Integer accountID, Integer count, Integer offset);
	/**
	 * Catches all games based on count and offset given.
	 * @param count - number of expected/max results. Max value of it is 1000
	 * @param offset - specifies after which entry should the counting begin
	 * @return can return null if list is empty
	 */
	public List<Game> getRecent(Integer count, Integer offset);
	
	/** Asks for author name */
	public String getAuthorName(Integer gameId);
}

