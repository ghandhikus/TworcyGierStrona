package com.clockwise.tworcy.util;

import java.security.AccessControlException;

import org.springframework.stereotype.Component;

import com.clockwise.tworcy.exception.ParameterTooLongException;
import com.clockwise.tworcy.model.account.Access;
import com.clockwise.tworcy.model.account.Account;
import com.clockwise.tworcy.model.game.Game;
import com.clockwise.tworcy.model.news.News;

public @Component class AccountPermissions {

	private void n(String msg) { throw new NullPointerException(Messages.getString(msg)); }
	private void a(String msg) { throw new AccessControlException(Messages.getString(msg)); }
	private void p(String msg) throws ParameterTooLongException { throw new ParameterTooLongException(Messages.getString(msg)); }
	
	// Single checks
	public void checkParameter(Account account) throws NullPointerException
	{
		if(account == null) n("Account.notSet");
		if(account.getId() == null) n("Account.notSetID");
		if(account.getId() == 0) n("Account.badID");
	}
	public void checkParameter(News news) throws NullPointerException, ParameterTooLongException
	{
		if(news == null) n("News.notSet");
		if(news.getContent() == null) n("News.notSetContent");
		if(news.getTitle() == null) n("News.notSetTitle");
		if(news.getTitle().length()>64) p("News.longTitle");
		if(news.getContent().length()>32767) p("News.longContent");
	}
	public void checkParameter(Game game) throws NullPointerException, ParameterTooLongException
	{
		if(game == null) n("Game.notSet");
		if(game.getDescription() == null) n("Game.notSetDesc");
		if(game.getTitle() == null) n("Game.notSetTitle");
		if(game.getTitle().length()>32) p("Game.longTitle");
		if(game.getDescription().length()>4096) p("Game.longDesc");
	}
	
	// Dual checks
	public void checkParameters(Game game, Account account) throws NullPointerException, ParameterTooLongException
	{
		checkParameter(game);
		checkParameter(account);
	}
	public void checkParameters(News news, Account account) throws NullPointerException, ParameterTooLongException
	{
		checkParameter(news);
		checkParameter(account);
	}
	
	// News checks
	public boolean isAuthor(News news, Account account)
	{
		return (account.getId().equals(news.getAuthorId()));
	}
	public boolean canAddNews(Account account)
	{
		return (account.getAccess() >= Access.MODERATOR.getAccess());
	}
	public boolean canUpdateNews(Account account)
	{
		return (account.getAccess() >= Access.MODERATOR.getAccess());
	}
	public boolean canRemoveNews(Account account)
	{
		return (account.getAccess() >= Access.MODERATOR.getAccess());
	}
	// Game checks
	public boolean isAuthor(Game game, Account account)
	{
		return (account.getId().equals(game.getAuthorId()));
	}
	public boolean canAddGames(Account account)
	{
		return (account.getAccess() >= Access.NORMAL.getAccess());
	}
	public boolean canUpdateGames(Account account)
	{
		return (account.getAccess() >= Access.MODERATOR.getAccess());
	}
	public boolean canRemoveGames(Account account)
	{
		return (account.getAccess() >= Access.MODERATOR.getAccess());
	}
	
	// News permissions
	public void checkAddingNews(News news, Account account) throws AccessControlException
	{
		if(!canAddNews(account))
			a("Access.newsAdd");
	}
	public void checkUpdatingNews(News news, Account account) throws AccessControlException
	{
		if(!isAuthor(news, account) && !canUpdateNews(account))
			a("Access.newsUpdate");
	}
	public void checkRemovingNews(News news, Account account) throws AccessControlException
	{
		if(!isAuthor(news, account) && !canRemoveNews(account))
			a("Access.newsRemove");
	}
	public void checkRemovingNews(Account account) throws AccessControlException
	{
		if(!canRemoveNews(account))
			a("Access.newsRemove");
	}
	
	// Game permissions
	public void checkAddingGame(Game game, Account account) throws AccessControlException
	{
		if(!canAddGames(account))
			a("Access.gameAdd");
	}
	public void checkUpdatingGame(Game game, Account account) throws AccessControlException
	{
		if(!isAuthor(game, account) && !canUpdateGames(account))
			a("Access.gameUpdate");
	}
	public void checkRemovingGame(Game game, Account account) throws AccessControlException
	{
		if(!isAuthor(game, account) && !canRemoveGames(account))
			a("Access.gameRemove");
	}
	public void checkRemovingGame(Account account) throws AccessControlException
	{
		if(!canRemoveGames(account))
			a("Access.gameRemove");
	}
}
