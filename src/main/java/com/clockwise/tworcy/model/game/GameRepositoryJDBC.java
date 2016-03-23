package com.clockwise.tworcy.model.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.clockwise.tworcy.model.account.AccountService;
import com.clockwise.tworcy.util.JSONUtils;
import com.mysql.jdbc.Messages;

@Repository class GameRepositoryJDBC implements GameRepository {
	/** Database object, is automatically injected by Spring */
	private @Autowired JdbcTemplate db;
	private @Autowired AccountService accounts;
	/** TODO: DOC */
	private @Autowired JSONUtils json;
	
	/** KeyHolder to catch id from insert statements. */
	private KeyHolder keyHolder = new GeneratedKeyHolder();
	
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
		String sql = "INSERT INTO games (authorId, dateAdded, title, description, media) VALUES (?, ?, ?, ?, ?)";

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
					ps.setString(5, json.stringArrayToJSON(game.getMedia()));

					return ps;
				}
			}, keyHolder);
	
			// Get id from db
			game.setGameId(((Long) keyHolder.getKey()).intValue());
		}
		
		return game;
	}

	@Override
	public Game update(Game game) {
		checkNulls(game);
		
		if(game.getDateAdded() == null) throw new NullPointerException(Messages.getString("Game.notSetDate"));
		game.setDateUpdated(DateTime.now());
		

		// Joda date to sql timestamp
		Timestamp timeStamp = new Timestamp(game.getDateUpdated().getMillis());
		
		// Update the game
		String sql = "UPDATE games SET authorId=?, dateUpdated=?, title=?, description=?, media=? WHERE id=? LIMIT 1";
		//int rowsAffected = 
		db.update(sql, game.getAuthorId(), timeStamp, game.getTitle(), game.getDescription(), json.stringArrayToJSON(game.getMedia()), game.getGameId());

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
		
		// Remove from the database
		String sql = "DELETE FROM games WHERE id = ? LIMIT 1";
		db.update(sql, id);
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
		String sql = "INSERT INTO games_archive(gameId, authorId, date, title, description, media)"+
				"SELECT id as gameId, authorId, date, title, description, media FROM games WHERE id = ? LIMIT 1";
		db.update(sql, id);
	}

	@Override
	public int getCount() {
		// Catch count of game.
		String sql = "SELECT COUNT(*) FROM games";
		try {
			return db.queryForObject(sql, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	@Override
	public Game getSpecific(Integer gameId) {
		if (gameId == null)
			return null;

		// Try to catch the specific or return null.
		try {
			String sql = "SELECT * FROM games WHERE id = ? ORDER BY dateUpdated, dateAdded DESC LIMIT 1";
			return db.queryForObject(sql, new Object[] { gameId }, new GameRepositoryJDBCMapper());
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

		String sql = "SELECT * FROM games WHERE authorId = ? ORDER BY dateUpdated, dateAdded DESC LIMIT ? OFFSET ?";

		List<Game> ret = (List<Game>) db.query(sql, new GameRepositoryJDBCMapper(), new Object[]{accountID,count,offset});
		
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
		
		// Get games
		String sql = "SELECT * FROM games ORDER BY dateUpdated, dateAdded DESC LIMIT ? OFFSET ?";
		List<Game> ret = (List<Game>) db.query(sql, new GameRepositoryJDBCMapper(),
				new Object[]{count, offset});
		
		if(ret.size()==0)
			return null;
		else
			return ret;
	}

	@Override
	public String getAuthorName(Integer gameId) {
		if(accounts.get(gameId)==null) return "Deleted Account";
		return accounts.get(gameId).getName();
	}
}
