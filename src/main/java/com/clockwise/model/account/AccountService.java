package com.clockwise.model.account;

import java.util.List;

import org.springframework.stereotype.Service;
import javax.servlet.http.HttpSession;

@Service
public interface AccountService {
	public void insert(Account player, String name, String hash);
	public void update(Account player);
	public void delete(Integer id);
	 
	public List<Account> getList();
	public Account get(Integer id);
	public Account get(String name);
	public Integer getNewUserCount();

	public boolean isLogged(HttpSession session);
	public Account getLogged(HttpSession session);
	public Account login(String name, String password, HttpSession session) throws Exception;
	public Account register(String name, String password, HttpSession session) throws Exception;
	public void logout(HttpSession session);
}
