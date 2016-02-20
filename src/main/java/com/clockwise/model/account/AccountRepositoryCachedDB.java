package com.clockwise.model.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository class AccountRepositoryCachedDB implements AccountRepository {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	// List cache
	private List<Account> cache = Collections.synchronizedList(new ArrayList<Account>());

	// KeyHolder to catch id from insert statements
	private KeyHolder keyHolder = new GeneratedKeyHolder();

	@Override
	public Account insert(Account account) {
		// Insert to db
		String sql = "INSERT INTO accounts (name, password) VALUES (?, ?)";
		// jdbcTemplate.update(sql, account.getName());

		// Prepared statement for catching the id
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
				// sql VALUES
				ps.setString(1, account.getName());
				ps.setString(2, account.getPasswordHash());
				return ps;
			}
		}, keyHolder);

		// Get id from db
		account.setId(((Long) keyHolder.getKey()).intValue());
		cache.add(account);
		
		// Debug
		System.out.println("Created Record entry name = " + account.getName());

		return account;
	}

	@Override
	public Account get(Integer id) {
		// Search through cache.
		for (Account account : cache)
			if (account.getId() == id) {
				return account;
			}

		// Try if there is account to return
		try {
			// Select from sql
			String sql = "SELECT * FROM accounts WHERE id = ? LIMIT 1";
			Account account = jdbcTemplate.queryForObject(sql, new Object[] { id }, new AccountRepositoryDBMapper());
			// Add to cache
			cache.add(account);
			
			// Debug
			System.out.println("Selected Record entry id = " + account.getId() + " name = " + account.getName());

			return account;

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Account get(String name) {
		// Search through cache.
		for (Account account : cache) {
			System.out.println("Player in cache : " + account.getName());
			if (account.getName().equals(name)) {
				return account;
			}
		}
		
		// Try if there is account to return
		try {
			// Select from db
			String sql = "SELECT * FROM accounts WHERE name = ? LIMIT 1";
			Account account = jdbcTemplate.queryForObject(sql, new Object[] { name }, new AccountRepositoryDBMapper());
			// Add to cache
			cache.add(account);
			
			// Debug
			System.out.println("Selected Record entry id = " + account.getId() + " name = " + account.getName());

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
		List<Account> account = (List<Account>) jdbcTemplate.queryForObject(sql, new AccountRepositoryDBMapper());

		return account;
	}

	@Override
	public void delete(Integer id) {
		// Remove account from cache
		for (Account account : cache)
			if (account.getId() == id) {
				cache.remove(account);
				break;
			}
		// Delete in db
		String sql = "DELETE FROM accounts WHERE id = ? LIMIT 1";
		jdbcTemplate.update(sql, id);

		// Debug
		System.out.println("Removed Record from Players where id = " + id);
	}

	@Override
	public void update(Account account) {
		// Update account in cache
		for (Account p : cache)
			if (p.getId() == account.getId()) {
				p.setName(account.getName());
				break;
			}
		// Update in db
		String sql = "UPDATE accounts SET name = ? WHERE id = ? LIMIT 1";
		jdbcTemplate.update(sql, account.getName(), account.getId());

		// Debug
		System.out.println("Updated Record with id = " + account.getId());
	}

}
