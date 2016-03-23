package com.clockwise.tworcy.modules.maren;

import java.util.Date;

public class Player {
	private String name;
	private Integer accountID;
	private int x =0;
	private int y =0;
	private Date lastUpdate = new Date();
	private float previousPing = 0;
	private float pingJumps = 0;

	public Integer getAccountID() {
		return accountID;
	}

	public void setAccountID(Integer accountID) {
		this.accountID = accountID;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	/*@SuppressWarnings("unchecked")
	public String toJSON()
	{
		Map<String,String> obj = new HashMap<>();

		obj.put("id", ""+accountID);
		obj.put("x", ""+x);
		obj.put("y", ""+y);
		
		return gson.to;
	}*/

	public float getPreviousPing() {
		return previousPing;
	}

	public void setPreviousPing(float previousPing) {
		this.previousPing = previousPing;
	}

	public float getPingJumps() {
		return pingJumps;
	}

	public void setPingJumps(float pingJumps) {
		this.pingJumps = pingJumps;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
