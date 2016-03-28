package com.clockwise.tworcy.model.game;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.clockwise.tworcy.model.account.AccountService;
import com.clockwise.tworcy.util.JSONUtils;
import com.mysql.jdbc.Messages;

@Transactional @Repository class GameRepositoryHibernate implements GameRepository {
	// Hibernate sessions
	private @Autowired SessionFactory sessionFactory;
	private @Autowired AccountService accounts;
	private @Autowired GameConverter convert;
	/** TODO: DOC */
	private @Autowired JSONUtils json;

	/** Quick hibernate session catcher */
	private Session getSession() { return sessionFactory.getCurrentSession(); }
	
	/** Logging library instance for this class */
	private static final Logger logger = Logger.getLogger(GameRepositoryHibernate.class);

	// Holds column names inside of table.
	private String gameTableFieldId;
	private String gameTableFieldAuthorId;
	private String gameTableFieldDateAdded;
	private String gameTableFieldDateUpdated;
	
	/**
	 * Checks for nulls inside the game model.
	 * @param game
	 */
	public void checkNulls(Game game) {
		if(game == null) throw new NullPointerException(Messages.getString("Game.notSet"));
		if(game.getAuthorId() == 0)  throw new IllegalArgumentException(Messages.getString("Game.notSetAcc"));
		if(game.getTitle() == null)  throw new NullPointerException(Messages.getString("Game.notSetTitle"));
		else if(game.getTitle().trim().length()==0)  throw new IllegalArgumentException(Messages.getString("Game.notSetTitle"));
		if(game.getDescription() == null)  throw new NullPointerException(Messages.getString("Game.notSetDesc"));
		else if(game.getDescription().trim().length()==0)  throw new IllegalArgumentException(Messages.getString("Game.notSetDesc"));
	}
	
	/**
	 * Populates internal strings for column names.
	 * @throws Exception occurs when reflection fails to detect column names from News using the {@link Column} annotation
	 */
	public GameRepositoryHibernate() throws Exception {
		try {
			gameTableFieldId = GameData.class.getDeclaredField("gameId").getName();
			gameTableFieldAuthorId = GameData.class.getDeclaredField("authorId").getName();
			gameTableFieldDateAdded = GameData.class.getDeclaredField("dateAdded").getName();
			gameTableFieldDateUpdated = GameData.class.getDeclaredField("dateUpdated").getName();

			logger.debug("gameTableFieldId : "+gameTableFieldId);
			logger.debug("gameTableFieldAuthorId : "+gameTableFieldAuthorId);
			logger.debug("gameTableFieldDateAdded : "+gameTableFieldDateAdded);
			logger.debug("gameTableFieldDateUpdated : "+gameTableFieldDateUpdated);
		} catch (NoSuchFieldException | SecurityException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	@Override
	public Game insert(Game game) {
		// Makes sure that parameters are correct
		checkNulls(game);
		
		// Start transaction
		Transaction tran = getSession().getTransaction();
		tran.begin();
		
		// Persist game data
		GameData data = convert.toData(game);
		getSession().persist(data);
		
		// Commit operation
		tran.commit();
		
		// Set the game
		convert.toGame(data, game);

		// Return
		return game;
	}

	@Override
	public Game update(Game game) {
		// Makes sure that parameters are correct
		checkNulls(game);
		if(game.getGameId() <= 0) 
			throw new NullPointerException(Messages.getString("Game.notSetId"));
		if(game.getDateAdded() == null)
			throw new NullPointerException(Messages.getString("Game.notSetDate"));
		game.setDateUpdated(DateTime.now());
		
		GameData data = convert.toData(game);
		getSession().update(data);
		
		return game;
	}

	public @Override void delete(Game game) {
		if (game == null)
			return;
		if (game.getGameId() <= 0)
			return;

		getSession().delete(convert.toData(game));
	}

	@Override
	public void archive(Game game) {
		// Makes sure that parameters are correct
		checkNulls(game);
		
		// Begins hibernate transaction
		Transaction transaction = getSession().getTransaction();
		transaction.begin();
		try {
			// Get the most recent data from repository
			GameData data = (GameData) getSession().createCriteria(GameData.class).add(Restrictions.eq(gameTableFieldId, game.getGameId())).uniqueResult();
			
			// Convert the data to archive
			GameArchiveData archive = convert.toArchive(data);
			
			// Archive the data
			getSession().persist(archive);
			
			// Apply changes to the database
			transaction.commit();
		} catch(Exception e) {
			// Rollback on error
			transaction.rollback();
			throw e;
		}
	}

	public @Override int getCount() {
		// Create criteria
		Criteria criteria = getSession().createCriteria(GameData.class);
		// Set expected return
		criteria.setProjection(Projections.rowCount());
		// Catch the return value
		Number result = (Number) criteria.uniqueResult();
		// Return the truncated value
		return result.intValue();
	}
	public @Override int getUserGameCount(Integer authorId) {
		if (authorId == null || authorId == 0) {
			logger.error("authorId is either null or 0. authorId = "+authorId);
			if (authorId == 0)
				throw new IllegalArgumentException("authorId = "+authorId+". Expected  not (null or 0)");
			else if(authorId == null)
				throw new NullPointerException("authorId is null");
		}
		// Create criteria
		Criteria criteria = getSession().createCriteria(GameData.class);
		// Set expected return
		criteria.setProjection(Projections.rowCount());
		// Limit the counting to single author only
		criteria.add(Restrictions.eq(gameTableFieldAuthorId, authorId));
		// Catch the return value
		Number result = (Number)criteria.uniqueResult();
		// Return the truncated value
		return result.intValue();
	}

	public @Override Game getSpecific(Integer gameId) {
		// Makes sure that parameters are correct
		if (gameId == null) return null;
		// Catch the data
		GameData data = (GameData) getSession().createCriteria(GameData.class).add(Restrictions.eq(gameTableFieldId, gameId)).uniqueResult();
		// Convert the data to the game
		return convert.toGame(data);
	}

	public @Override List<Game> getUserGames(Integer authorId, Integer count, Integer offset) {
		// Makes sure that parameters are correct
		if (authorId == null || authorId == 0) return null;
		if (count == null) count = 1;
		if (offset == null) offset = 0;
		if (count > 1000) count = 1000;

		// Catches newsCount to determine if it's even possible to catch so many results
		int newsCount = getCount();
		// Make sure that offset won't be higher than newsCount
		if (offset > newsCount) offset = newsCount - 1;
		// There is no news in db so no need to get anything
		if (newsCount == 0) return null;

		try {
			Criteria crit = getSession().createCriteria(GameData.class);
			crit.setMaxResults(count);
			crit.setFirstResult(offset);
			crit.add(Restrictions.eq(gameTableFieldAuthorId, authorId));
			crit.addOrder(Order.desc(gameTableFieldDateUpdated));
			crit.addOrder(Order.desc(gameTableFieldDateAdded));
			crit.addOrder(Order.desc(gameTableFieldId));
			
			// Conversion
			@SuppressWarnings("unchecked")
			List<GameData> dataList = (List<GameData>) crit.list();
			List<Game> gameList = new ArrayList<Game>(dataList.size());
			for(GameData data : dataList)
				gameList.add(convert.toGame(data));
			
			return gameList;
		} catch ( HibernateException e ) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<Game> getRecent(Integer count, Integer offset) {
		// Makes sure that parameters are correct
		if (count == null) count = 10;
		if (offset == null) offset = 0;
		if (count > 1000) count = 1000;

		int gameCount = getCount();
		// Make sure that offset won't be higher than newsCount
		if (offset > gameCount) offset = gameCount - 1;
		// There is no news in db so no need to get anything
		if (gameCount == 0) return null;

		
		try {
			Criteria crit = getSession().createCriteria(GameData.class);
			crit.setMaxResults(count);
			crit.setFirstResult(offset);
			crit.addOrder(Order.desc(gameTableFieldDateUpdated));
			crit.addOrder(Order.desc(gameTableFieldDateAdded));
			crit.addOrder(Order.desc(gameTableFieldId));

			// Conversion
			@SuppressWarnings("unchecked")
			List<GameData> dataList = (List<GameData>) crit.list();
			List<Game> gameList = new ArrayList<Game>(dataList.size());
			for (GameData data : dataList)
				gameList.add(convert.toGame(data));
			
			return gameList;
		} catch ( HibernateException e ) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public String getAuthorName(Integer gameId) {
		if(accounts.get(gameId)==null) return "Deleted Account";
		return accounts.get(gameId).getName();
	}
}
