package com.clockwise.tworcy.model.account;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Daniel
 */
@WebAppConfiguration // MVC
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring-dispatcher-servlet.xml"})
@Transactional
public class AccountDAOTests {
	@Autowired AccountDAO accounts;
	
	final String username;
	final String password;
	
	{
		SecureRandom random = new SecureRandom();
		// Generates random string
		String name = new BigInteger(130, random).toString(32);
		// trims it to 16-5 characters
		username = "test_"+name.substring(0, Math.min(name.length(), 16-5));
		
		name = new BigInteger(400, random).toString(32);
		password = "TESTS_ACCOUNT_"+name.substring(0, Math.min(name.length(), 64-14*2))+"_TESTS_ACCOUNT";
	}
	
	/**
	 * CRUD - Create, Read, Update, Delete
	 */
	public @Rollback(true) @Test void CRUD() {
		Account acc = accounts.create(username, password);
		// Create
		acc = createChecks(acc);
		
		// Read
		acc = readChecks(acc);
		
		// Update
		acc = updateChecks(acc);
		
		// Delete
		acc = deleteChecks(acc);
	}
	
	public Account createChecks(Account acc) {
		assertNotNull("AccountDAO return null on create", acc);
		return acc;
	}
	public Account readChecks(Account acc) {
		acc = accounts.get(acc.getId());
		assertNotNull("AccountDAO returns null when catching it by id", acc);
		acc = accounts.get(username);
		assertNotNull("AccountDAO returns null when catching it by the username", acc);
		return acc;
	}
	public Account updateChecks(Account acc) {
		// TODO: account data updating
		return acc;
	}
	public Account deleteChecks(Account acc) {
		int id = acc.getId();
		accounts.delete(id);
		acc = accounts.get(id);
		assertNull("Can still catch account after deleting", acc);
		acc = accounts.get(username);
		assertNull("Can still catch account after deleting", acc);
		return acc;
	}
}
