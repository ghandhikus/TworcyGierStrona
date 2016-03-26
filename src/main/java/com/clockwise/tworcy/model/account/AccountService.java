package com.clockwise.tworcy.model.account;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

public @Service interface AccountService extends UserDetailsService {
	void update(Account player);
	void delete(Integer id);
	 
	List<Account> getList(Integer count, Integer offset);
	Account get(Integer id);
	Account get(String name);

	Account getLogged();
	boolean isLogged();
	
	/*boolean isLogged(HttpSession session);
	Account getLogged(HttpSession session);
	Account login(String name, String password, HttpSession session) throws Exception;
	void logout(HttpSession session);*/
	Account login(String name, String password, HttpServletRequest request) throws Exception;
	Account register(String name, String password) throws Exception;
	void logout();
}
