package com.clockwise.tworcy.modules.maren;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.clockwise.tworcy.model.account.Account;

/**
 * Defines game state.
 * @author Daniel
 *
 */
public class Game {
	Map<Account, Player> playerList = Collections.synchronizedMap(new HashMap<Account, Player>());
	Map<Integer, CheatStrikes> cheatStrikes = Collections.synchronizedMap(new HashMap<Integer, CheatStrikes>());
	
	private final float speed = 300;
	private final int width = 500;
	private final int height = 500;
	
	private final int maxCheatStrikes = 10;
	private final int pardonCheatStrikeAfter = 750;
	
	private final int disconnectAfter = 2500;
	
	public float getSpeed() { return speed; }
	public float getWidth() { return width; }
	public float getHeight() { return height; }
	
	/**
	 * Adds player to the game.
	 * @param account
	 */
	public void login(Account account)
	{
		if(get(account) == null)
			newPlayer(account);
	}
	
	/**
	 * {@link Player} factory. Creates player from account id and sets the position randomly based on the size of the game.
	 * @see #getWidth()
	 * @see #getHeight()
	 * @param account
	 * @return
	 */
	public Player newPlayer(Account account)
	{
		Player player = new Player();
		player.setAccountID(account.getId());
		player.setX((int) (Math.random()*width));
		player.setY((int) (Math.random()*height));
		player.setName(account.getName());

		synchronized(playerList)
		{
			playerList.put(account, player);
		}
		return player;
	}
	
	/**
	 * Catches {@link Player} from the list.
	 * @param account the account bound to {@link Player} by {@link Account#getId()}.
	 * @return null if player not found
	 */
	public Player get(Account account)
	{
		checkRemoval();
		Player ret = null;
		synchronized(playerList)
		{
			for(Entry<Account, Player> player : playerList.entrySet())
				if(player.getValue().getAccountID() == account.getId())
				{
					ret = player.getValue();
					break;
				}
		}
		return ret;
	}
	
	/**
	 * Factory method, creates current list of logged-in players.
	 * @see {@link #login(Account)} for logging in players.
	 * @return {@link List}<{@link Player}> - created {@link ArrayList} of players
	 */
	@SuppressWarnings("javadoc")
	public List<Player> getPlayerList()
	{
		checkRemoval();
		List<Player> list = new ArrayList<>();

		synchronized(playerList)
		{
			for(Entry<Account, Player> player : playerList.entrySet())
				list.add(player.getValue());
		}
		return list;
	}
	
	/**
	 * Get a difference between two dates.
	 * @param oldest the oldest date
	 * @param newest the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public long getDateDiff(Date oldest, Date newest, TimeUnit timeUnit) {
	    long diffInMillies = newest.getTime() - oldest.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Checks if {@link Player} was not updated longer than 1s, also removes him if(lastUpdate>{@link #disconnectAfter}).
	 * @see {@link Player#getLastUpdate()}
	 * @param player
	 */
	public void checkRemovalSingle(Player player)
	{
		long lastUpdate = getDateDiff(player.getLastUpdate(), new Date(), TimeUnit.MILLISECONDS);
		
		if(lastUpdate>disconnectAfter)
			logout(player);
	}
	
	/**
	 * Checks if all logged in players were not updated for more than 1s.
	 * <br />
	 * Uses {@link #checkRemovalSingle(Player)} on every logged in {@link Player}.
	 */
	public void checkRemoval()
	{
		synchronized(playerList)
		{
			for(Entry<Account, Player> entry : playerList.entrySet())
				checkRemovalSingle(entry.getValue());
		}
	}
	
	/**
	 * Removes {@link Player} from logged in.
	 * @param player {@link Player} that will be logged out.
	 */
	public void logout(Player player)
	{
		synchronized(playerList)
		{
			for(Entry<Account, Player> entry : playerList.entrySet())
			{
				if(entry.getValue().getAccountID()==player.getAccountID())
				{
					playerList.remove(entry.getKey());
					break;
				}
			}
		}
		
		cheatStrikes.remove(player.getAccountID());
	}
	
	/**
	 * Call it after getting data from the player.
	 * <br />
	 * - Checks if player has more than 8 cheat strikes.
	 * - Removes 4 strikes a second. Each one per {@link #pardonCheatStrikeAfter} ms.
	 * @param player
	 * @throws Cheater
	 */
	public void update(Player player) throws Cheater
	{
		long lastUpdate = getDateDiff(player.getLastUpdate(), new Date(), TimeUnit.MILLISECONDS);

		CheatStrikes currentStreak = cheatStrikes.get(player.getAccountID());
		// No streaks for this player yet
		if(currentStreak!=null)
		{
			currentStreak.timer+=lastUpdate;

			if(currentStreak.count>=maxCheatStrikes)
				throw new Cheater("Streak count reached "+maxCheatStrikes);
			
			// Each pardonCheatStrikeAfter in ms
			if(currentStreak.timer>=pardonCheatStrikeAfter)
			{
				currentStreak.timer-=pardonCheatStrikeAfter;
				
				if(currentStreak.count>0)
					currentStreak.count--;
			}
				
		}
	}
	
	public void cheatStrike(Player player, int count)
	{
		CheatStrikes currentStreak = cheatStrikes.get(player.getAccountID());
		if(currentStreak == null)
			cheatStrikes.put(player.getAccountID(), currentStreak = new CheatStrikes());

		currentStreak.count+=count;
	}
	
	public void cheatStrike(Player player)
	{
		cheatStrike(player, 1);
	}
	
	public int getCheatStrikes(Player player)
	{
		CheatStrikes currentStreak = cheatStrikes.get(player.getAccountID());

		if(currentStreak == null)
			return 0;
		else
			return currentStreak.count;
	}
	
	private class CheatStrikes
	{
		public int count = 0;
		public long timer = 0;
	}

	public void calcPing(Player player) {
		Date oldTime = player.getLastUpdate();
		Date newTime = new Date();
		float currentPing = (float) ((double)getDateDiff(oldTime, newTime, TimeUnit.NANOSECONDS)/1000000d);
		
		float previousPing = player.getPreviousPing();
		float averagePing = (currentPing+previousPing)/2f;
		player.setPreviousPing(averagePing);
		

		float pingJump = Math.abs(previousPing-currentPing);
		player.setPingJumps(pingJump);
	}
	public double getPingAverage(Player player) {
		return player.getPreviousPing();
	}
}
