package com.clockwise.tworcy.model.account;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public @Transactional @Repository class AccountDAOJDBC implements AccountDAO {
	
	private @Autowired JdbcTemplate jdbcTemplate;

	private static final Logger logger = Logger.getLogger(AccountDAOJDBC.class);
	
	public @Override Account create(String username, String password) {
		if(username == null || password == null) return null;
		// Insert to db
		String sql = "INSERT INTO accounts (username, password, enabled, locked) VALUES (?, ?, ?, ?)";
		jdbcTemplate.update(sql, username, password, true, false);

		return get(username);
	}

	@Override
	public Account get(Integer id) {
		// Try if there is account to return
		try {
			// Select from sql
			String sql = "SELECT * FROM accounts WHERE id = ? LIMIT 1";
			Account account = jdbcTemplate.queryForObject(sql, new Object[] { id }, new AccountDAOJDBCMapper());

			return account;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Account get(String username) {
		// Try if there is account to return
		try {
			// Select from db
			String sql = "SELECT * FROM accounts WHERE username = ? LIMIT 1";
			Account account = jdbcTemplate.queryForObject(sql, new Object[] { username }, new AccountDAOJDBCMapper());

			return account;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Account> getList() {
		// Select from db
		String sql = "SELECT * FROM accounts LIMIT 10000";

		@SuppressWarnings("unchecked")
		List<Account> account = (List<Account>) jdbcTemplate.queryForObject(sql, new AccountDAOJDBCMapper());

		return account;
	}

	@Override
	public void delete(Integer id) {
		// Delete in db
		String sql = "DELETE FROM accounts WHERE id = ? LIMIT 1";
		jdbcTemplate.update(sql, id);

		// Debug
		logger.debug("Removed Record from Players where id = " + id);
	}

	@Override
	public void update(Account account) {
		// Update in db
		String sql = "UPDATE accounts SET username = ? WHERE id = ? LIMIT 1";
		jdbcTemplate.update(sql, account.getName(), account.getId());

		// Debug
		logger.debug("Updated Record with id = " + account.getId());
	}
}
