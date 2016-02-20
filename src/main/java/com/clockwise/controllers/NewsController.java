package com.clockwise.controllers;

import java.security.AccessControlException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.clockwise.exceptions.ParameterTooLongException;
import com.clockwise.model.account.Account;
import com.clockwise.model.account.AccountService;
import com.clockwise.model.news.News;
import com.clockwise.model.news.NewsService;

@Controller
@RequestMapping(value = "/news")
public class NewsController {

	@Autowired NewsService newsService;
	@Autowired AccountService accounts;
	
	@RequestMapping("/")
	public String index() { return "redirect:/news/list"; }
	
	@RequestMapping("/get/{id}")
	public ModelAndView newsGet(@PathVariable("id") Integer id, HttpSession session) {
		Account account = accounts.getLogged(session);
		ModelAndView ret = new ModelAndView("core");
		ret.addObject("account", account);
		ret.addObject("news", newsService.getSpecificByID(id));
		return ret;
	}
	
	@RequestMapping({"/list", "/list/"})
	public ModelAndView newsList(HttpServletRequest request) { return newsList(0, request); }
	
	@RequestMapping("/list/{offset}")
	public ModelAndView newsList(@PathVariable(value="offset") Integer offset, HttpServletRequest request) {
		Account account = accounts.getLogged(request.getSession());
		
		ModelAndView ret = new ModelAndView("core");
		ret.addObject("newsList", newsService.getRecentNews(10, offset));
		ret.addObject("newsCount", newsService.getNewsCount());
		ret.addObject("account", account);

		return ret;
	}

	@RequestMapping({"/new", "/new/"})
	public ModelAndView newNews(HttpServletRequest request, HttpServletResponse response) {
		Account account = accounts.getLogged(request.getSession());
		if(account==null) return new ModelAndView("redirect:/account/login?ret=/news/new");
		
		ModelAndView ret = new ModelAndView("core");
		ret.addObject("account", account);
		
		// TODO: Load cache data from previous form POST, after redirecting here

		return ret;
	}
	
	@RequestMapping(value = {"/new", "/new/"}, method = RequestMethod.POST)
	public ModelAndView newNews(String title, String content, HttpServletRequest request) {
		Account account = accounts.getLogged(request.getSession());
		// TODO: Hold data before redirecting to login
		if(account==null) return new ModelAndView("redirect:/account/login?ret=/news/new/");
		
		try
		{
			newsService.createNews(title, content, account);

			return new ModelAndView("redirect:/news/list");
		}
		catch(AccessControlException | NullPointerException | ParameterTooLongException e)
		{
			ModelAndView ret = new ModelAndView("core");
			ret.addObject("account", account);
			ret.addObject("errors", e.getMessage());

			return ret;
		}
	}

	@RequestMapping({"/edit", "/edit/"})
	public ModelAndView editNews()
	{
		return new ModelAndView("redirect:/news/list");
	}
	
	@RequestMapping("/edit/{ID}")
	public ModelAndView editNews(@PathVariable(value="ID") Integer id, HttpServletRequest request)
	{
		Account account = accounts.getLogged(request.getSession());
		if(account==null) return new ModelAndView("redirect:/account/login?ret=/news/edit/"+id);
		// TODO: Load cache data from previous form POST, after redirecting here
		
		try
		{
			if(newsService.canEdit(account, id)) {
				News news = newsService.getSpecificByID(id);
				
				ModelAndView ret = new ModelAndView("core");
				ret.addObject("account", account);
				ret.addObject("news", news);
		
				return ret;
			}
		}
		catch(NullPointerException | AccessControlException | ParameterTooLongException e)
		{
			ModelAndView ret = new ModelAndView("core");
			ret.addObject("account", account);
			ret.addObject("error", e.getMessage());
	
			return ret;
		}
		
		return new ModelAndView("redirect:/news/list");
	}
	@RequestMapping(value = "/edit/{ID}", method = RequestMethod.POST)
	public ModelAndView editNews(@PathVariable(value="ID") Integer id,
			String title, String content, HttpServletRequest request)
	{
		Account account = accounts.getLogged(request.getSession());
		// TODO: Hold data before redirecting to login
		if(account==null) return new ModelAndView("redirect:/account/login?ret=/news/edit/"+id);
		
		try
		{
			newsService.updateNews(id, title, content, account);

			return new ModelAndView("redirect:/news/list");
		}
		catch(AccessControlException | NullPointerException | ParameterTooLongException e)
		{
			ModelAndView ret = new ModelAndView("core");
			ret.addObject("account", account);
			ret.addObject("errors", e.getMessage());

			return ret;
		}
	}
}
