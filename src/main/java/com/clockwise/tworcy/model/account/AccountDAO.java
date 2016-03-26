package com.clockwise.tworcy.model.account;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository interface AccountDAO {
	   public Account create(String username, String password);
	   public void update(Account account);
	   public void delete(Integer id);
	   
	   public Account get(Integer id);
	   public Account get(String name);
	   public List<Account> getList(Integer count, Integer offset);
}
