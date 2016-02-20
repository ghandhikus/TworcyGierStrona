package com.clockwise.model.game;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.jdbc.core.RowMapper;

class GameRepositoryDBMapper implements RowMapper<Game> {

	@Override
	public Game mapRow(ResultSet r, int id) throws SQLException {

		Game obj = new Game();
		obj.setGameId(r.getInt("id"));
		obj.setAuthorId(r.getInt("authorID"));
		obj.setDateAdded(new DateTime(r.getTimestamp("dateAdded")));
		obj.setDateUpdated(new DateTime(r.getTimestamp("dateUpdated")));
		obj.setTitle(r.getString("title"));
		obj.setDescription(r.getString("description"));
		obj.setImages(parseImages(r.getString("images")));
		
		return obj;
	}

	private List<String> parseImages(String json) {
		// Checks if json is null
		if(json == null) {
			return null;
		} else {
			// Attempt to parse the json
			try {
				// Json parser
				JSONParser parser = new JSONParser();
				// Attempts to parse or throw ParseException
				JSONArray parsed = (JSONArray) parser.parse(json);
				// Hold the results
				List<String> list = new ArrayList<String>();
				
				// Exception below for this
				@SuppressWarnings("unchecked")
				Iterator<String> iterator = parsed.iterator();
				// Add the results to the 
				while (iterator.hasNext()) list.add(iterator.next());
				
				// Synchronized list for the safety
				return Collections.synchronizedList(list);
			} catch (ClassCastException | ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
