package com.clockwise.tworcy.model.game;

import java.security.AccessControlException;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clockwise.tworcy.exception.ParameterTooLongException;
import com.clockwise.tworcy.model.account.Account;
import com.clockwise.tworcy.util.AccountPermissions;
import com.clockwise.tworcy.util.Messages;

public @Service("gameService") class GameServiceHibernate implements GameService {

	private @Autowired GameRepository db;
	private @Autowired AccountPermissions permissions;

	private void prepareForAdding(Game game, Account account) {
		game.setDateAdded(DateTime.now());
		game.setDateUpdated(null);
		game.setAuthorId(account.getId());
	}
	
	public @Override Game addGame(Game game, Account account) throws AccessControlException, ParameterTooLongException {
		permissions.checkParameters(game, account);
		permissions.checkAddingGame(game, account);
		prepareForAdding(game, account);
		
		return db.insert(game);
	}

	public @Override Game createGame(String title, String description, Account account) throws AccessControlException, ParameterTooLongException {
		Game game = new Game();
		game.setTitle(title);
		game.setDescription(description);
		
		return addGame(game, account);
	}

	public @Override Game updateGame(Game game, Account account) throws AccessControlException, ParameterTooLongException {
		permissions.checkParameters(game, account);
		permissions.checkUpdatingGame(game, account);
		
		db.update(game);
		
		return db.getSpecific(game.getGameId());
	}

	public @Override void removeBy(Game game, Account account) throws AccessControlException, ParameterTooLongException {
		permissions.checkParameters(game, account);
		permissions.checkRemovingGame(game, account);
		
		db.delete(game);
	}

	public @Override Integer getGameCount() {
		return db.getCount();
	}

	public @Override Game getSpecificByID(Integer gameId) {
		if(gameId == null) throw new NullPointerException(Messages.getString("Game.notSetID"));
		
		return db.getSpecific(gameId);
	}

	public @Override List<Game> getUserGames(Account account, Integer count, Integer offset) {
		if(count == null) count = 10;
		if(offset == null) offset = 0;
		permissions.checkParameter(account); // checks null and id
		
		return db.getUserGames(account.getId(), count, offset);
	}

	public @Override List<Game> getRecentGames(Integer count, Integer offset) {
		if(count == null) count = 10;
		if(offset == null) offset = 0;
		
		return db.getRecent(count, offset);
	}

	public @Override boolean canEdit(Account account, Integer id) throws NullPointerException, ParameterTooLongException {
		permissions.checkParameter(account); // checks null and id
		Game game = getSpecificByID(id);
		permissions.checkParameter(game);
		return !(!permissions.isAuthor(game, account) && !permissions.canUpdateGames(account));
	}

	public @Override String getAuthorName(Integer gameId) {
		return db.getAuthorName(gameId);
	}

	public @Override String getAuthorName(Game game) {
		return db.getAuthorName(game.getAuthorId());
	}
}
