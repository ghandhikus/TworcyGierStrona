package com.clockwise.controllers;

import java.io.IOException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.clockwise.exceptions.BadImageException;
import com.clockwise.exceptions.ParameterTooLongException;
import com.clockwise.model.account.Account;
import com.clockwise.model.account.AccountService;
import com.clockwise.model.game.Game;
import com.clockwise.model.game.GameService;
import com.clockwise.util.AccountPermissions;
import com.clockwise.util.ImageUtils;

@RequestMapping("/game")
public @Controller class Games {

	@Autowired AccountPermissions permissions;
	@Autowired AccountService accounts;
	@Autowired GameService games;
	@Autowired ImageUtils imageUtils;

	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("redirect:/game/list");
	}

	@RequestMapping("/get/{id}")
	public ModelAndView getGame(@PathVariable("id") Integer id) {
		ModelAndView modelAndView = new ModelAndView("core");
		
		Game game = games.getSpecificByID(id);
		modelAndView.addObject("game", game);

		return modelAndView;
	}

	@RequestMapping({"/new", "/new/"})
	public ModelAndView newGame(HttpSession session) {
		// Get the account
		Account account = accounts.getLogged(session);
		// Redirect to main site if account was not found
		if(account == null) return new ModelAndView("redirect:/account/login?ret=/game/new");

		// TODO: Load cache data from previous form POST, after redirecting here
		ModelAndView modelAndView = new ModelAndView("core");
		modelAndView.addObject("account", account);
		return modelAndView;
	}
	
	@RequestMapping(value = {"/new", "/new/"}, method = RequestMethod.POST)
	public ModelAndView newGame(String title, String description, HttpSession session) {
		// Get the account
		Account account = accounts.getLogged(session);
		// TODO: Hold data before redirecting to login
		// Redirect to login
		if(account == null) return new ModelAndView("redirect:/account/login?ret=/game/new");

		// Create the game
		try {
			// Attempt to create the game
			Game game = games.createGame(title, description, account);
			// Redirect to the game on success
			return new ModelAndView("redirect:/game/get/"+game.getGameId());
		} catch (AccessControlException | ParameterTooLongException e) {
			// Something went wrong, back to editor
			ModelAndView modelAndView = new ModelAndView("core");
			modelAndView.addObject("gameTitle", title);
			modelAndView.addObject("gameDesc", description);
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("account", account);
			return modelAndView;
		}
		
	}
	

	@RequestMapping("/edit/{id}")
	public ModelAndView editGame(@PathVariable("id") Integer id, HttpSession session) {
		// Get the account
		Account account = accounts.getLogged(session);
		// Redirect to main site if account was not found
		if(account == null) return new ModelAndView("redirect:/account/login?ret=/game/edit/"+id);
		// TODO: Load cache data from previous form POST, after redirecting here

		// TODO: error handling
		// Update the game
		Game game = games.getSpecificByID(id);
		ModelAndView modelAndView = new ModelAndView("core");
		modelAndView.addObject("game", game);
		modelAndView.addObject("account", account);
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
	public ModelAndView editGame(@PathVariable("id") Integer id, String title, String description,
			@RequestParam(value = "image", required = false) List<MultipartFile> images, HttpSession session) {
		// Get the account
		Account account = accounts.getLogged(session);
		// TODO: Hold data before redirecting to login
		// Redirect to login
		if(account == null) return new ModelAndView("redirect:/account/login?ret=/game/edit/"+id);

		try {
			// Check if account has access to upload images for this game
			permissions.checkUpdatingGame(games.getSpecificByID(id), account);
			
			// Image uploading
			List<String> addedImages = new ArrayList<String>();
			
			for(MultipartFile image : images)
				if (image != null && !image.isEmpty()) {
					// Validates image, throws exception on fail
					imageUtils.validateImage(image);
					// Save the image
					String img = imageUtils.saveImage("games/"+id, image);
					if(img!=null)
						addedImages.add(img);
				}
			
			
			// Update the game
			Game game = games.updateGame(id, title, description, addedImages, account);
			// Redirect to the game on success
			return new ModelAndView("redirect:/game/get/"+game.getGameId());
		} catch (AccessControlException | ParameterTooLongException | BadImageException | IOException e) {
			// Something went wrong, back to editor
			ModelAndView modelAndView = new ModelAndView("core");
			modelAndView.addObject("gameTitle", title);
			modelAndView.addObject("gameDesc", description);
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("account", account);
			return modelAndView;
		}
	}
	
	@RequestMapping({"/list", "/list/"})
	public ModelAndView listGames() { return listGames(10, 0); }
	
	@RequestMapping("/list/{offset}")
	public ModelAndView listGames(Integer count, @PathVariable("offset") Integer offset) {
		ModelAndView modelAndView = new ModelAndView("core");
		
		List<Game> list = games.getRecentGames(count, offset);
		modelAndView.addObject("gameList", list);
		if(list != null)
			modelAndView.addObject("gameCount", list.size());
		
		return modelAndView;
	}
	
}
