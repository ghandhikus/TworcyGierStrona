package com.clockwise.tworcy.model.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clockwise.tworcy.util.JSONUtils;

@Service class GameConverter {

	private @Autowired JSONUtils json;
	GameData toData(Game game) {
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
