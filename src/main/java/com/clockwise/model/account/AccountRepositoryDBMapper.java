package com.clockwise.model.account;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class AccountRepositoryDBMapper implements RowMapper<Account> {
	@Override
	public Account mapRow(ResultSet r, int id) throws SQLException {
		Account player = new Account();
		player.setId(r.getInt("id"));
		player.setName(r.getString("name"));
		player.setPasswordHash(r.getString("password"));
		player.setAccessLevel(Access.byValue(r.getInt("access")));
		return player;
	}

}
