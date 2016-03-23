package com.clockwise.tworcy.model.game;

import java.util.List;

import org.joda.time.DateTime;

/**
 * Defines Game added by users of the page.
 * @author Daniel
 *
 */
public class Game {
	private Integer gameId;
	private Integer authorId;
	private List<String> media;
	private String title;
	private String description;
	private DateTime dateAdded;
	private DateTime dateUpdated;

	public Integer getGameId() {
		return gameId;
	}

	public Integer getAuthorId() {
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

	
	void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public void setMedia(List<String> media) {
		this.media = media;
	}
	
	void setTitle(String title) {
		this.title = title;
	}

	void setDescription(String description) {
		this.description = description;
	}

	void setDateAdded(DateTime dateAdded) {
		this.dateAdded = dateAdded;
	}

	void setDateUpdated(DateTime dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
}
