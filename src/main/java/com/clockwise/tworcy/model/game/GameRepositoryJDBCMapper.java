package com.clockwise.tworcy.model.game;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.RowMapper;

import com.clockwise.tworcy.controllers.Games;
import com.google.gson.Gson;

class GameRepositoryJDBCMapper implements RowMapper<Game> {

	private final Gson gson = new Gson();
	
	@Override
	public Game mapRow(ResultSet r, int id) throws SQLException {

		Game obj = new Game();
		obj.setGameId(r.getInt("id"));
		obj.setAuthorId(r.getInt("authorID"));
		obj.setDateAdded(new DateTime(r.getTimestamp("dateAdded")));
		obj.setDateUpdated(new DateTime(r.getTimestamp("dateUpdated")));
		obj.setTitle(r.getString("title"));
		obj.setDescription(r.getString("description"));
		obj.setMedia(parseMedia(r.getString("media")));
		
		return obj;
	}

	private List<String> parseMedia(String json) {
		// Checks if json is null
		if(json == null) {
			return null;
		} else {
			String[] arr = gson.fromJson(json, String[].class);
			// Attempt to parse the json
			List<String> parsed = new ArrayList<String>();
			for(String str : arr) parsed.add(str);
			
			// Synchronized list for the safety
			return Collections.synchronizedList(parsed);
		}
	}
}
