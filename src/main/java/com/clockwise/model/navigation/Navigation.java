package com.clockwise.model.navigation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Holds HashMap bounding variable names to sites. It is case sensitive.
 * <br>
 * Example:
 * <br>
 * <br>
 * variable:<br>
 * - login <br>
 * site:<br>
 * - /account/login <br>
 * <br>
 * 
 * @author Daniel
 */
public @Component("nav") class Navigation {
	
	/** Contains navigation paths as a value. Keys are variable names. */
	private Map<String, String> nav = Collections.synchronizedMap(new HashMap<String, String>()); 

	public Navigation() {
		// Core
		add("home","/");
		add("maren","/maren/");
		add("panel","/panel/");
		
		// News
		add("news","/news/");
		add("specificNews","/news/get/");
		add("newsSpecific","/news/get/");
		add("listNews","/news/list");
		add("editNews","/news/edit");
		add("newNews","/news/new");
		add("newsList","/news/list");
		add("newsEdit","/news/edit");
		add("newsNew","/news/new");
		
		// Games
		add("game","/game/");
		add("games","/game/");
		add("specificGame","/game/get/");
		add("gameSpecific","/game/get/");
		add("listGames","/game/list");
		add("editGame","/game/edit");
		add("newGame","/game/new");
		add("gamesList","/game/list");
		add("gameEdit","/game/edit");
		add("gameNew","/game/new");
		
		// Accounts
		add("register","/account/register");
		add("login","/account/login");
		add("logout","/account/logout");
		add("profile","/account/profile/");
	}
	
	/** Adds the variable and path to the hashmap */
	private void add(String variable, String path) { nav.put(variable, path); }
	/** @see Navigation#getPath(String variable) */
	public String get(String variable) { return getPath(variable); }
	
	/**
	 * Catches the path for the given variable.<br>
	 * Does replace the path like spring:url does.<br>
	 * Use {@link #getRawPath} to get the raw path.<br>
	 * <br>
	 * Example:<br>
	 * <br>
	 * path:<br>
	 * /accounts/login<br>
	 * <br>
	 * return:<br>
	 * /SiteName/Something/accounts/login
	 */
	public String getPath(String variable) { return ServletUriComponentsBuilder.fromCurrentContextPath().path(nav.get(variable)).build().toUriString(); }
	/** Catches the raw path for the given variable. */
	public String getRawPath(String variable) { return nav.get(variable); }
	/** Catches the variable by the given path. */
	public String getVariable(String path) { return getKeyByValue(nav, path); }
	
	private String removePagePrefix(String page) {
		String prefix = ServletUriComponentsBuilder.fromCurrentContextPath().path("").build().toUriString();
		//System.out.println("Prefix : "+prefix);
		//System.out.println("PagePre : "+page);
		page = page.replace(prefix, "");
		//System.out.println("PagePost: "+page);
		return page;
	}
	
	public boolean onPage(String currentPage, String variable) {
		String path = getRawPath(variable);
		currentPage = removePagePrefix(currentPage);
		//System.out.println("RawPath : "+path);
		
		boolean comparison = currentPage.startsWith(path);
		//System.out.println("onPageResult : "+comparison);
		return comparison;
	}
	
	public boolean onPageSpecific(String currentPage, String variable) {
		String path = getRawPath(variable);
		currentPage = removePagePrefix(currentPage);
		//System.out.println("RawPath : "+path);
		
		boolean comparison = currentPage.equalsIgnoreCase(path);
		//System.out.println("onPageResult : "+comparison);
		return comparison;
	}
	
	/** Iterates through Map to find the key. */
	private <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
}
