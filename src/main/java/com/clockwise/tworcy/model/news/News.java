package com.clockwise.tworcy.model.news;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "News")
public class News implements Serializable {
	private static final long serialVersionUID = 3037628203079572107L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "newsid", nullable = false)
	private int newsID;
	
	@Column(name = "authorid", nullable = false)
	private int authorID;
	
	@Column(name = "date", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime date;
	
	@Column(name = "title", nullable = false)
	private String title;
	
	@Column(name = "content", nullable = false)
	private String content;
	

	public int getNewsID() {
		return newsID;
	}
	public int getAuthorID() {
		return authorID;
	}
	public DateTime getDate() {
		return date;
	}
	public String getTitle() {
		return title;
	}
	public String getContent() {
		return content;
	}
	

	void setNewsID(int newsID) {
		this.newsID = newsID;
	}
	void setAuthorID(int authorID) {
		this.authorID = authorID;
	}
	void setDate(DateTime date) {
		this.date = date;
	}
	void setTitle(String title) {
		this.title = title;
	}
	void setContent(String content) {
		this.content = content;
	}
}
