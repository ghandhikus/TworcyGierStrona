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

	// Holds column names inside the News table.
	private String gameTableFieldId;
	private String gameTableFieldAuthorId;
	private String gameTableFieldDateAdded;
	private String gameTableFieldDateUpdated;
	
	public void checkNulls(Game game) {
		if(game == null) throw new NullPointerException(Messages.getString("Game.notSet"));
		if(game.getAuthorId() == 0)  throw new NullPointerException(Messages.getString("Game.notSetAcc"));
		if(game.getTitle() == null || game.getTitle().trim().length()==0)  throw new NullPointerException(Messages.getString("Game.notSetTitle"));
		if(game.getDescription() == null || game.getDescription().trim().length()==0)  throw new NullPointerException(Messages.getString("Game.notSetDesc"));
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
		checkNulls(game);

		// Set initial dates
		game.setDateAdded(DateTime.now());
		game.setDateUpdated(null);
		
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

		return game;
	}

	@Override
	public Game update(Game game) {
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
		// Checking parameters
		checkNulls(game);
		
		Transaction transaction = getSession().getTransaction();
		transaction.begin();
		try {
			// Get the most recent
			GameData data = (GameData) getSession().createCriteria(GameData.class).add(Restrictions.eq(gameTableFieldId, game.getGameId())).uniqueResult();
			
			GameArchiveData archive = convert.toArchive(data);
			
			getSession().persist(archive);
			
			transaction.commit();
		} catch(Exception e) {
			// Rollback on error
			transaction.rollback();
			throw e;
		}
	}

	public @Override int getCount() {
		return ((Number)getSession().createCriteria(GameData.class).setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	public @Override Game getSpecific(Integer gameId) {
		if (gameId == null) return null;
		
		GameData data = (GameData) getSession().createCriteria(GameData.class).add(Restrictions.eq(gameTableFieldId, gameId)).uniqueResult();
		
		return convert.toGame(data);
	}

	@Override
	public List<Game> getUserGames(Integer authorId, Integer count, Integer offset) {
		if (authorId == null || authorId == 0) return null;
		if (count == null) count = 1;
		if (offset == null) offset = 0;
		if (count > 1000) count = 1000;

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
