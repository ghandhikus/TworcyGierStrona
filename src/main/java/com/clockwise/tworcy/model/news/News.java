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
	@Column(name = "newsId", nullable = false)
	private int newsId;
	
	@Column(name = "authorId", nullable = false)
	private int authorId;
	
	@Column(name = "date", nullable = false)
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime date;
	
	@Column(name = "title", nullable = false)
	private String title;
	
	@Column(name = "content", nullable = false)
	private String content;
	

	public int getNewsId() {
		return newsId;
	}
	public int getAuthorId() {
		return authorId;
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
	

	void setNewsId(int newsId) {
		this.newsId = newsId;
	}
	void setAuthorId(int authorId) {
		this.authorId = authorId;
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
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();

		b.append("News {\n");
		b.append("  newsId = ").append(newsId).append("\n");
		b.append("  authorId = ").append(authorId).append("\n");
		b.append("  date = ").append(date).append("\n");
		b.append("  title = ").append(title.substring(0, Math.min(100,title.length()))).append("\n");
		b.append("  content = ").append(content.substring(0, Math.min(100,content.length()))).append("\n");
		b.append("}");
		
		return b.toString();
	}
}
