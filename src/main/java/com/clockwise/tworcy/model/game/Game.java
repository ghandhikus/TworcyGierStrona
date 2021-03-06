package com.clockwise.tworcy.model.game;

import java.util.List;

import org.joda.time.DateTime;

/**
 * Defines Game added by users of the page.
 * @author Daniel
 *
 */
public class Game {
	private int gameId;
	private int authorId;
	private List<String> media;
	private String title;
	private String description;
	private DateTime dateAdded;
	private DateTime dateUpdated;

	public int getGameId() {
		return gameId;
	}

	public int getAuthorId() {
		return authorId;
	}

	public List<String> getMedia() {
		return media;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public DateTime getDateAdded() {
		return dateAdded;
	}

	public DateTime getDateUpdated() {
		return dateUpdated;
	}

	
	void setGameId(int gameId) {
		this.gameId = gameId;
	}

	void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public void setMedia(List<String> media) {
		this.media = media;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	void setDateAdded(DateTime dateAdded) {
		this.dateAdded = dateAdded;
	}

	void setDateUpdated(DateTime dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public void setDefault() {
		gameId = 0;
		authorId = 0;
		media = null;
		title = null;
		description = null;
		dateAdded = null;
		dateUpdated = null;
	}
}
