package com.clockwise.tworcy.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.clockwise.tworcy.model.account.AccountService;
import com.clockwise.tworcy.model.news.NewsService;

public @RequestMapping("/") @Controller class Index {

	@Autowired AccountService accounts;
	@Autowired NewsService news;
	
	public @RequestMapping("/") ModelAndView home(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		ModelAndView ret = new ModelAndView("core");
		ret.addObject("account", accounts.getLogged());
		ret.addObject("news", news.getRecentNews(10, 0));
		return ret;
	}
}
