package com.clockwise.model.account;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository interface AccountRepository {
	   public Account insert(Account player);
	   public void update(Account player);
	   public void delete(Integer id);
	   
	   public Account get(Integer id);
	   public Account get(String name);
	   public List<Account> getList();
}
