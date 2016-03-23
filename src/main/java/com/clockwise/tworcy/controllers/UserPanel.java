package com.clockwise.tworcy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.clockwise.tworcy.model.account.Account;
import com.clockwise.tworcy.model.account.AccountService;

@Controller
@RequestMapping(value = "/panel")
public class UserPanel {
	
	@Autowired AccountService accounts;
	
	@RequestMapping("/")
	public ModelAndView home() {
		Account acc = accounts.getLogged();
		
		if(acc==null) return new ModelAndView("redirect:/");
		
		// Logged in, show page.
		ModelAndView ret = new ModelAndView("core");
		ret.addObject("account", acc);
		return ret;
	}
}
