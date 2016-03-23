package com.clockwise.tworcy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clockwise.tworcy.model.account.Account;
import com.clockwise.tworcy.model.account.AccountService;
import com.clockwise.tworcy.model.news.NewsService;

public @RequestMapping("/Access_Denied") @Controller class AccessDenied {

	@Autowired AccountService accounts;
	@Autowired NewsService news;
	
	@RequestMapping({"","/"})
	public @ResponseBody String home() {
		Account acc = accounts.getLogged();
		if(acc!=null)
			System.out.println("Someone got Access Denied\n"+acc.toString());
		return "<html><body><h1>Access Denied</h1></body></html>";
	}
}
