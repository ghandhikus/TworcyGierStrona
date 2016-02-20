package com.clockwise.model.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.clockwise.model.account.AccountService;
import com.mysql.jdbc.Messages;

@Repository class GameRepositoryCachedDB implements GameRepository {
	/** Database object, is automatically injected by Spring */
	private @Autowired JdbcTemplate db;
	private @Autowired AccountService accounts;
	
	/** Cached value, specifies number of records in database. */
	private volatile Integer gamesCount = null;
	/** List cache. Requires {@link synchronize} block to ensure that it remains thread safe. */
	private List<Game> cache = Collections.synchronizedList(new ArrayList<Game>());
	/** KeyHolder to catch id from insert statements. */
	private KeyHolder keyHolder = new GeneratedKeyHolder();

	/**
	 * Is called by Spring after it sets @{@link Autowired} for database.
	 * <br>
	 * Prepares cache.
	 */
	public @PostConstruct void init() {
		if (gamesCount == null)
			forceUpdateCount();
	}

	/**
	 * Forces counting of game from the database.
	 */
	public void forceUpdateCount() {
		// Catch count of game.
		String sql = "SELECT COUNT(*) FROM games";
		try {
			gamesCount = db.queryForObject(sql, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			gamesCount = 0;
		}
	}
	
	public void checkNulls(Game game) {
		if(game == null) throw new NullPointerException(Messages.getString("Game.notSet"));
		if(game.getAuthorId() == null)  throw new NullPointerException(Messages.getString("Game.notSetAcc"));
		if(game.getTitle() == null || game.getTitle().trim().length()==0)  throw new NullPointerException(Messages.getString("Game.notSetTitle"));
		if(game.getDescription() == null || game.getDescription().trim().length()==0)  throw new NullPointerException(Messages.getString("Game.notSetDesc"));
	}
	
	@Override
	public Game insert(Game game) {
		checkNulls(game);

		// Set initial dates
		game.setDateAdded(DateTime.now());
		game.setDateUpdated(null);

		// Insert statement for game
		String sql = "INSERT INTO games (authorId, dateAdded, title, description, images) VALUES (?, ?, ?, ?, ?)";

		// Convert game time to sql timestamp
		Timestamp timeStamp = new Timestamp(game.getDateAdded().getMillis());

		// Thread safety for keyholder
		synchronized(keyHolder)
		{
			// Prepared statement for catching the id
			db.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
					// game table values
					// Ordered from 1!
					ps.setInt(1, game.getAuthorId());
					ps.setTimestamp(2, timeStamp);
					ps.setString(3, game.getTitle());
					ps.setString(4, game.getDescription());
					ps.setString(5, arrToJSON(game.getImages()));

					return ps;
				}
			}, keyHolder);
	
			// Get id from db
			game.setGameId(((Long) keyHolder.getKey()).intValue());
		}
		
		// Cache results
		synchronized(cache) {
			cache.add(game);
		}
		gamesCount++;
		
		return game;
	}

	@Override
	public Game update(Game game) {
		checkNulls(game);
		
		if(game.getDateAdded() == null) throw new NullPointerException(Messages.getString("Game.notSetDate"));
		game.setDateUpdated(DateTime.now());
		

		// Joda date to sql timestamp
		Timestamp timeStamp = new Timestamp(game.getDateUpdated().getMillis());
		
		// Update cache
		synchronized(cache)
		{
			for(Game cur : cache)
				if(cur.getGameId() == game.getGameId())
				{
					// Do nothing if it is the same object
					if(cur == game) break;
					
					// Set data to cache object
					cur.setAuthorId(game.getAuthorId());
					cur.setDateUpdated(game.getDateUpdated());
					cur.setTitle(game.getTitle());
					cur.setDescription(game.getDescription());
					cur.setImages(game.getImages());
					// Break iteration
					break;
				}
		}

		
		// Update the game
		String sql = "UPDATE games SET authorId=?, dateUpdated=?, title=?, description=?, images=? WHERE id=? LIMIT 1";
		//int rowsAffected = 
		db.update(sql, game.getAuthorId(), timeStamp, game.getTitle(), game.getDescription(), arrToJSON(game.getImages()), game.getGameId());

		return game;
	}

	public @Override void delete(Game game) {
		if (game == null)
			return;
		if (game.getGameId() == null)
			return;

		delete(game.getGameId());
	}

	public @Override void delete(Integer id) {
		if (id == null)
			return;
		
		synchronized(cache)
		{
			for(Game cur : cache)
				if(cur.getGameId() == id)
				{
					cache.remove(cur);
					break;
				}
		}
		
		// Delete and substract the games count from cache.
		String sql = "DELETE FROM games WHERE id = ? LIMIT 1";
		int rowsAffected = db.update(sql, id);
		gamesCount -= rowsAffected;
	}

	@Override
	public void archive(Game game) {
		// Checking parameters
		checkNulls(game);
		archive(game.getGameId());
	}

	@Override
	public void archive(Integer id) {
		if(id == null) throw new NullPointerException(Messages.getString("Game.notSetID"));
		
		// Archive current news
		String sql = "INSERT INTO games_archive(gameID, authorID, date, title, description, images)"+
				"SELECT id as gameID, authorID, date, title, description, images FROM games WHERE id = ? LIMIT 1";
		db.update(sql, id);
	}

	@Override
	public int getCount() {
		if (gamesCount == null)
			throw new NullPointerException("Database is badly configured. gamesCount was not loaded.");
		return gamesCount;
	}

	@Override
	public Game getSpecific(Integer gameId) {
		if (gameId == null)
			return null;

		// There is no games in db so no need to get anything
		if (gamesCount == 0) return null;

		// Search through cache
		synchronized(cache) {
			for(Game game : cache) if(game.getGameId().equals(gameId)) return game;
		}
		// Try to catch the specific or return null.
		try {
			String sql = "SELECT * FROM games WHERE id = ? ORDER BY dateUpdated, dateAdded DESC LIMIT 1";
			return db.queryForObject(sql, new Object[] { gameId }, new GameRepositoryDBMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Game> getUserGames(Integer accountID, Integer count, Integer offset) {
		if (accountID == null) return null;
		if (count == null) count = 1;
		if (offset == null) offset = 0;
		if (count > 1000) count = 1000;
		// Make sure that offset won't be higher than gamesCount
		if (offset > gamesCount) offset = gamesCount - 1;
		// There is no games in db so no need to get anything
		if (gamesCount == 0) return null;

		String sql = "SELECT * FROM games WHERE authorId = ? ORDER BY dateUpdated, dateAdded DESC LIMIT ? OFFSET ?";

		List<Game> ret = (List<Game>) db.query(sql, new GameRepositoryDBMapper(), new Object[]{accountID,count,offset});
		
		if(ret.size()==0)
			return null;
		else
			return ret;
	}

	@Override
	public List<Game> getRecent(Integer count, Integer offset) {
		if (count == null) count = 10;
		if (offset == null) offset = 0;
		if (count > 1000) count = 1000;
		// Make sure that offset won't be higher than gamesCount
		if (offset > gamesCount) offset = gamesCount - 1;
		// There is no games in db so no need to get anything
		if (gamesCount == 0) return null;

		// Cache won't work here, as this method works with db offsets
		
		// Get games
		String sql = "SELECT * FROM games ORDER BY dateUpdated, dateAdded DESC LIMIT ? OFFSET ?";
		List<Game> ret = (List<Game>) db.query(sql, new GameRepositoryDBMapper(),
				new Object[]{count, offset});
		
		if(ret.size()==0)
			return null;
		else
			return ret;
	}

	@Override
	public String getAuthorName(Integer gameId) {
		return accounts.get(gameId).getName();
	}

	private String arrToJSON(List<String> images) {
		if(images == null) {
			return null;
		}
		
		String json = JSONArray.toJSONString(images);

		return json;
	}
}
