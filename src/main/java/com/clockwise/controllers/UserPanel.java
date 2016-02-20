package com.clockwise.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.clockwise.model.account.Account;
import com.clockwise.model.account.AccountService;

@Controller
@RequestMapping(value = "/panel")
public class UserPanel {
	
	@Autowired AccountService accounts;
	
	@RequestMapping("/")
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
		Account acc = accounts.getLogged(request.getSession());
		
		if(acc==null) return new ModelAndView("redirect:/");
		
		// Logged in, show page.
		ModelAndView ret = new ModelAndView("core");
		ret.addObject("account", acc);
		return ret;
	}
}
