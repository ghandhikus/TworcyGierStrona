package com.clockwise.tworcy.model.game;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Defines Game added by users of the page.
 * @author Daniel
 *
 */
@Entity
@Table(name = "GameArchiveData")
public class GameArchiveData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;

	@Column(name = "gameId", nullable = false)
	private int gameId;
	
	@Column(name = "authorId", nullable = false)
	private int authorId;
	
	@Column(name = "media", nullable = true)
	private String media;
	
	@Column(name = "title", nullable = false)
	private String title;
	
	@Column(name = "description", nullable = false)
	private String description;
	
	@Column(name = "dateAdded", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime dateAdded;
	
	@Column(name = "dateUpdated", nullable = true)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime dateUpdated;

	public int getId() {
		return id;
	}
	public int getGameId() {
		return gameId;
	}

	public int getAuthorId() {
		return authorId;
	}

	public String getMedia() {
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


	void setId(int id) {
		this.id = id;
	}
	
	void setGameId(int gameId) {
		this.gameId = gameId;
	}

	void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public void setMedia(String media) {
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
