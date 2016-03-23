package com.clockwise.tworcy.controllers;

import java.io.IOException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.clockwise.tworcy.exception.BadImageException;
import com.clockwise.tworcy.exception.InvalidYoutubeLink;
import com.clockwise.tworcy.exception.ParameterTooLongException;
import com.clockwise.tworcy.model.account.Account;
import com.clockwise.tworcy.model.account.AccountService;
import com.clockwise.tworcy.model.game.Game;
import com.clockwise.tworcy.model.game.GameService;
import com.clockwise.tworcy.util.AccountPermissions;
import com.clockwise.tworcy.util.ImageUtils;
import com.clockwise.tworcy.util.JSONUtils;
import com.mysql.jdbc.Messages;

@RequestMapping("/game")
public @Controller class Games {

	private @Autowired AccountPermissions permissions;
	private @Autowired AccountService accounts;
	private @Autowired GameService games;
	private @Autowired ImageUtils imageUtils;
	private @Autowired JSONUtils json;
	
	private String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
    private Pattern compiledPattern = Pattern.compile(pattern);

	@RequestMapping("/")
	public ModelAndView index() {
		System.out.println("test1");
		return new ModelAndView("redirect:/game/list");
	}

	@RequestMapping("/get/{id}")
	public ModelAndView getGame(@PathVariable("id") Integer id) {
		System.out.println("test2");
		ModelAndView modelAndView = new ModelAndView("core");

		Game game = games.getSpecificByID(id);
		modelAndView.addObject("game", game);
		modelAndView.addObject("account", accounts.getLogged());

		return modelAndView;
	}

	@RequestMapping({ "/new", "/new/" })
	public ModelAndView newGame() {
		System.out.println("test3");
		// Get the account
		Account account = accounts.getLogged();
		// Redirect to main site if account was not found
		if (account == null)
			return new ModelAndView("redirect:/account/login?ret=/game/new");

		// TODO: Load cache data from previous form POST, after redirecting here
		ModelAndView modelAndView = new ModelAndView("core");
		modelAndView.addObject("account", account);
		return modelAndView;
	}

	@RequestMapping(value = { "/new", "/new/" }, method = RequestMethod.POST)
	public ModelAndView newGame(String title, String description) {
		System.out.println("test4");
		// Get the account
		Account account = accounts.getLogged();
		// TODO: Hold data before redirecting to login
		// Redirect to login
		if (account == null)
			return new ModelAndView("redirect:/account/login?ret=/game/new");

		// Create the game
		try {
			// Attempt to create the game
			Game game = games.createGame(title, description, account);
			// Redirect to the game on success
			return new ModelAndView("redirect:/game/get/" + game.getGameId());
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
	
	@RequestMapping("/edit/")
	public ModelAndView editGame() {
		return new ModelAndView("redirect:/");
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public ModelAndView editGame(@PathVariable("id") Integer id) {
		System.out.println("Wut");
		// Get the account
		Account account = accounts.getLogged();
		// Redirect to main site if account was not found
		if (account == null)
			return new ModelAndView("redirect:/account/login?ret=/game/edit/" + id);
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
	public ModelAndView editGame(
			@PathVariable("id") Integer id,
			String title,
			String description,
			String mediaJSON,
			String youtube,
			@RequestParam(value = "image", required = false) List<MultipartFile> image)
	{
		System.out.println("wat");
		// TODO: use imageOrderJSON
		// Get the account
		Account account = accounts.getLogged();
		// TODO: Hold data before redirecting to login
		// Redirect to login
		if (account == null)
			return new ModelAndView("redirect:/account/login?ret=/game/edit/" + id);

		try {
			Game game = games.getSpecificByID(id);
			
			// Check if account has access to upload images for this game
			permissions.checkUpdatingGame(game, account);
			
			// Image uploading
			List<String> addedImages = new ArrayList<String>();
			{
				// Iterate through image uploads
				for (MultipartFile img : image)
					if (img != null && !img.isEmpty()) {
						// Validates image, throws exception on fail
						imageUtils.validateImage(img);
						// Save the image
						String i = imageUtils.saveImage("games/" + id, img);
						if (i != null)
							addedImages.add(i);
					}
			}
			
			List<String> order = json.stringListFromJSON(mediaJSON);
			
			// Adding uploaded images
			{
				List<String> gameMedia = game.getMedia();
				
				order.addAll(addedImages);
				gameMedia.addAll(addedImages);
				
				game.setMedia(gameMedia);
			}
			
			// Adding youtube video
			if(youtube!=null && youtube.length()>5)
			{
				// Matcher with youtube regex
				Matcher matcher = compiledPattern.matcher(youtube);
				// Check if the image is correct
				if(matcher.find())
					// Add the correct youtube link
					game.getMedia().add(matcher.group());
				else
					// Invalid youtube link
					throw new InvalidYoutubeLink(Messages.getString("Exception.badYoutube"));
			}
			
			// Sort images
			{
				// Remove duplicates
				order = new ArrayList<>(new LinkedHashSet<>(order));
				// Check if images exist
				order = imageUtils.removeNonExistentImages(order, "games/"+id, "yt:");
				// TODO: find a way to move setImages to private
				game.setMedia(order);
			}
			
			// Update the game
			game = games.updateGame(game, account);
			
			// Redirect to the game on success
			return new ModelAndView("redirect:/game/get/" + game.getGameId());
			
		} catch (AccessControlException | ParameterTooLongException | BadImageException | IOException e) {
			// Something went wrong, back to the editor
			ModelAndView modelAndView = new ModelAndView("core");
			modelAndView.addObject("gameTitle", title);
			modelAndView.addObject("gameDesc", description);
			modelAndView.addObject("game", games.getSpecificByID(id));
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("account", account);
			return modelAndView;
		}
	}

	@RequestMapping({ "/list", "/list/" })
	public ModelAndView listGames() {
		return listGames(10, 0);
	}

	@RequestMapping("/list/{offset}")
	public ModelAndView listGames(Integer count,
			@PathVariable("offset") Integer offset) {
		ModelAndView modelAndView = new ModelAndView("core");

		List<Game> list = games.getRecentGames(count, offset);
		modelAndView.addObject("gameList", list);
		if (list != null)
			modelAndView.addObject("gameCount", list.size());
		modelAndView.addObject("account", accounts.getLogged());

		return modelAndView;
	}

}
