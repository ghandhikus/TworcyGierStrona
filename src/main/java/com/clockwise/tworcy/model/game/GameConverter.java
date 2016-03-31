package com.clockwise.tworcy.model.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.clockwise.tworcy.util.JSONUtils;

@Component class GameConverter {

	private @Autowired JSONUtils json;

	boolean areValuesCorrect(Game obj) {
		if(obj == null) return false;
		if(obj.getAuthorId() == 0) return false;
		if(obj.getTitle() == null) return false;
		if(obj.getDescription() == null) return false;
		if(obj.getDateAdded() == null) return false;
		return true;
	}
	boolean areValuesCorrect(GameData obj) {
		if(obj == null) return false;
		if(obj.getAuthorId() == 0) return false;
		if(obj.getTitle() == null) return false;
		if(obj.getDescription() == null) return false;
		if(obj.getDateAdded() == null) return false;
		return true;
	}
	boolean areValuesCorrect(GameArchiveData obj) {
		if(obj == null) return false;
		if(obj.getGameId() == 0) return false;
		if(obj.getAuthorId() == 0) return false;
		if(obj.getTitle() == null) return false;
		if(obj.getDescription() == null) return false;
		if(obj.getDateAdded() == null) return false;
		return true;
	}
	
	GameData toData(Game game) {
		if(!areValuesCorrect(game)) return null;
		GameData data = new GameData();
		data.setGameId(game.getGameId());
		data.setAuthorId(game.getAuthorId());
		data.setMedia(json.stringArrayToJSON(game.getMedia()));
		data.setTitle(game.getTitle());
		data.setDescription(game.getDescription());
		data.setDateAdded(game.getDateAdded());
		data.setDateUpdated(game.getDateUpdated());
		return data;
	}
	GameData toData(Game game, GameData data) {
		if(!areValuesCorrect(game)) return null;
		if(!areValuesCorrect(data)) data = new GameData();
		data.setGameId(game.getGameId());
		data.setAuthorId(game.getAuthorId());
		data.setMedia(json.stringArrayToJSON(game.getMedia()));
		data.setTitle(game.getTitle());
		data.setDescription(game.getDescription());
		data.setDateAdded(game.getDateAdded());
		data.setDateUpdated(game.getDateUpdated());
		return data;
	}
	GameData toData(GameArchiveData archive) {
		if(!areValuesCorrect(archive)) return null;
		GameData data = new GameData();
		data.setGameId(archive.getGameId());
		data.setAuthorId(archive.getAuthorId());
		data.setMedia(archive.getMedia());
		data.setTitle(archive.getTitle());
		data.setDescription(archive.getDescription());
		data.setDateAdded(archive.getDateAdded());
		data.setDateUpdated(archive.getDateUpdated());
		return data;
	}
	
	Game toGame(GameData data) {
		if(!areValuesCorrect(data)) return null;
		Game game = new Game();
		game.setGameId(data.getGameId());
		game.setAuthorId(data.getAuthorId());
		game.setMedia(json.stringListFromJSON(data.getMedia()));
		game.setTitle(data.getTitle());
		game.setDescription(data.getDescription());
		game.setDateAdded(data.getDateAdded());
		game.setDateUpdated(data.getDateUpdated());
		return game;
	}
	Game toGame(GameData data, Game game) {
		if(!areValuesCorrect(data)) return null;
		if(!areValuesCorrect(game)) game = new Game();
		game.setGameId(data.getGameId());
		game.setAuthorId(data.getAuthorId());
		game.setMedia(json.stringListFromJSON(data.getMedia()));
		game.setTitle(data.getTitle());
		game.setDescription(data.getDescription());
		game.setDateAdded(data.getDateAdded());
		game.setDateUpdated(data.getDateUpdated());
		return game;
	}
	
	GameArchiveData toArchive(GameData data) {
		if(!areValuesCorrect(data)) return null;
		GameArchiveData archive = new GameArchiveData();
		archive.setGameId(data.getGameId());
		archive.setAuthorId(data.getAuthorId());
		archive.setMedia(data.getMedia());
		archive.setTitle(data.getTitle());
		archive.setDescription(data.getDescription());
		archive.setDateAdded(data.getDateAdded());
		archive.setDateUpdated(data.getDateUpdated());
		
		return archive;
	}
}
