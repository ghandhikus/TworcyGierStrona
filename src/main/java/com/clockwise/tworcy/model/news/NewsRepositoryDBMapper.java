package com.clockwise.tworcy.model.news;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.DateTime;
import org.springframework.jdbc.core.RowMapper;

/**
 * Acts as a mapper between database table and object data.
 * @author Daniel
 */
class NewsRepositoryDBMapper implements RowMapper<News> {
	
	/**
	 * Is fired for each result from database.
	 */
	public @Override News mapRow(ResultSet r, int id) throws SQLException {
		News obj = new News();
		obj.setNewsID(r.getInt("id"));
		obj.setAuthorID(r.getInt("authorID"));
		obj.setDate(new DateTime(r.getTimestamp("date")));
		obj.setTitle(r.getString("title"));
		obj.setContent(r.getString("content"));
		return obj;
	}

}
