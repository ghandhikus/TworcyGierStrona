package com.clockwise.controllers;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.clockwise.model.account.AccountService;

@RequestMapping("/account")
public @Controller class Accounts {

	@Autowired AccountService accounts;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView newAccount(String name, String password, HttpSession session) throws Exception {
		if (accounts.isLogged(session))
			return new ModelAndView("redirect:/");

		try {
			accounts.register(name, password, session);
		} catch (Exception e) {
			return register(e.getMessage());
		}

		return register(null);
	}

	@RequestMapping("/register")
	public ModelAndView register(String error) {
		ModelAndView ret = new ModelAndView("core");
		ret.addObject("error", error);
		ret.addObject("newUserCount", accounts.getNewUserCount());
		return ret;
	}

	@RequestMapping("/login")
	public ModelAndView loginForm(String error, HttpSession session) {
		if (accounts.isLogged(session)) return new ModelAndView("redirect:/");

		ModelAndView ret = new ModelAndView("core");
		ret.addObject("error", error);
		ret.addObject("newUserCount", accounts.getNewUserCount());
		return ret;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(String name, String password,
			@RequestParam(value = "ret", required = false) String GET_ret, HttpSession session) {
		
		// Check if already logged in
		if (accounts.isLogged(session)) {
			if(GET_ret == null)
				return new ModelAndView("redirect:/");
			else
				return new ModelAndView("redirect:"+GET_ret);
		}
		
		// Login or return him to login
		try {
			// Attempt to log in
			accounts.login(name, password, session);
			
			// Make sure that he's logged in
			if (accounts.isLogged(session)) {
				if(GET_ret == null)
					return new ModelAndView("redirect:/");
				else
					return new ModelAndView("redirect:"+GET_ret);
			}
			
		} catch (Exception e) {
			return loginForm(e.getMessage(), session);
		}
		
		return loginForm(null, session);
	}

	@RequestMapping("/logout")
	public ModelAndView logout(HttpSession session) {
		accounts.logout(session);
		return new ModelAndView("redirect:/");
	}

	@RequestMapping("/login_ajax")
	@SuppressWarnings("unchecked")
	public @ResponseBody String ajaxIsLogged(HttpSession session)
	{
		JSONArray json = new JSONArray();
		// Check if account is logged in
		if(accounts.getLogged(session) == null) json.add("notLogged");
		
		return json.toJSONString();
	}
}
