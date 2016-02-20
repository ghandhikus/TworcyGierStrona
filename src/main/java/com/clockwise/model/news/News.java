package com.clockwise.model.news;

import org.joda.time.DateTime;

public class News {
	private Integer newsID;
	private Integer authorID;
	private DateTime date;
	private String title;
	private String content;
	

	public Integer getNewsID() {
		return newsID;
	}
	public Integer getAuthorID() {
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
	

	void setNewsID(Integer newsID) {
		this.newsID = newsID;
	}
	void setAuthorID(Integer authorID) {
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
