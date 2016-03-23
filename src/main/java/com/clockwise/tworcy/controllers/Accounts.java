package com.clockwise.tworcy.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.clockwise.tworcy.model.account.AccountService;
import com.google.gson.Gson;

@RequestMapping("/account")
public @Controller class Accounts {

	private @Autowired AccountService accounts;
	private final Gson gson = new Gson();
	private final String ajaxNull = gson.toJson(new String[]{""});
	private final String ajaxNotLogged = gson.toJson(new String[]{"notLogged"});

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView newAccount(String name, String password) throws Exception {
		if (accounts.isLogged())
			return new ModelAndView("redirect:/");

		try {
			accounts.register(name, password);
		} catch (Exception e) {
			return register(e.getMessage());
		}

		return register(null);
	}

	@RequestMapping("/register")
	public ModelAndView register(String error) {
		ModelAndView ret = new ModelAndView("core");
		ret.addObject("error", error);
		return ret;
	}

	@RequestMapping("/login")
	public ModelAndView loginForm(String error, HttpServletRequest request) {
		if (accounts.isLogged()) return new ModelAndView("redirect:/");

		ModelAndView ret = new ModelAndView("core");
		ret.addObject("error", error);
		return ret;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(String name, String password,
			@RequestParam(value = "ret", required = false) String GET_ret, HttpServletRequest request) {
		
		// Check if already logged in
		if (accounts.isLogged()) {
			if(GET_ret == null)
				return new ModelAndView("redirect:/");
			else
				return new ModelAndView("redirect:"+GET_ret);
		}
		
		// Login or return him to login
		try {
			// Attempt to log in
			accounts.login(name, password, request);
			
			// Make sure that he's logged in
			if (accounts.isLogged()) {
				if(GET_ret == null)
					return new ModelAndView("redirect:/");
				else
					return new ModelAndView("redirect:"+GET_ret);
			}
			
		} catch (Exception e) {
			return loginForm(e.getMessage(), request);
		}
		
		return loginForm("Can't login, server error.", request);
	}

	@RequestMapping("/logout")
	public ModelAndView logout(HttpSession session) {
		accounts.logout();
		return new ModelAndView("redirect:/");
	}
	@RequestMapping("/login_ajax")
	public @ResponseBody String ajaxIsLogged(HttpSession session)
	{
		// Check if account is logged in
		if(!accounts.isLogged())
			return ajaxNotLogged;
		else
			return ajaxNull;
	}
}
