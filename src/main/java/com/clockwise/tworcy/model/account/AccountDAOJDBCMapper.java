package com.clockwise.tworcy.model.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;

import com.clockwise.tworcy.model.account.authorizations.AdminRole;
import com.clockwise.tworcy.model.account.authorizations.HeadAdminRole;
import com.clockwise.tworcy.model.account.authorizations.ModeratorRole;
import com.clockwise.tworcy.model.account.authorizations.NormalRole;

public class AccountDAOJDBCMapper implements RowMapper<Account> {
	@Override
	public Account mapRow(ResultSet r, int id) throws SQLException {
		
		// Last Login
		DateTime lastlogin = null;
		if (r.getTimestamp("lastlogin") != null)
			lastlogin = new DateTime(r.getTimestamp("lastlogin"));

		// Last password change
		DateTime lastpasswordchange = null;
		if (r.getTimestamp("lastpasswordchange") != null)
			lastpasswordchange = new DateTime(
					r.getTimestamp("lastpasswordchange"));

		// Account expires on
		DateTime expireon = null;
		if (r.getTimestamp("expireon") != null)
			expireon = new DateTime(r.getTimestamp("expireon"));

		// Password expires on
		DateTime passwordexpireon = null;
		if (r.getTimestamp("passwordexpireon") != null)
			passwordexpireon = new DateTime(r.getTimestamp("passwordexpireon"));
		
		// Granting authorities
		Collection<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
		int access = r.getInt("access");
		if(access >= Access.NORMAL.getAccess()) auth.add(new NormalRole());
		if(access >= Access.MODERATOR.getAccess()) auth.add(new ModeratorRole());
		if(access >= Access.ADMIN.getAccess()) auth.add(new AdminRole());
		if(access >= Access.HEADADMIN.getAccess()) auth.add(new HeadAdminRole());
		
		Account account = new Account(r.getInt("id"), r.getString("username"),
				r.getString("password"), lastlogin, lastpasswordchange, expireon, passwordexpireon,
				r.getBoolean("enabled"), r.getBoolean("locked"), auth);
		return account;
	}

}
