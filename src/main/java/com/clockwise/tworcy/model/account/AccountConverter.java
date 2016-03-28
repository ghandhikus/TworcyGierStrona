package com.clockwise.tworcy.model.account;

import static org.springframework.util.Assert.notNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.clockwise.tworcy.model.account.authorizations.AdminRole;
import com.clockwise.tworcy.model.account.authorizations.HeadAdminRole;
import com.clockwise.tworcy.model.account.authorizations.ModeratorRole;
import com.clockwise.tworcy.model.account.authorizations.NormalRole;

@Component class AccountConverter {

	/**
	 * Converts the list of {@link AccountData} to {@link Account Accounts}
	 * @param data list which contains {@link AccountData}
	 * @return list containing converted {@link Account Accounts}
	 */
	List<Account> convert(List<AccountData> data) {
		// Holding array
		ArrayList<Account> accounts = new ArrayList<>(data.size());
		// Converting loop
		for(int i=0;i<data.size();i++)
			accounts.set(i, convert(data.get(i)));
		// Return
		return accounts;
	}
	
	/**
	 * Converts {@link AccountData} to {@link Account}
	 * @param data object containing raw information about the {@link Account}
	 * @return converted {@link Account}
	 */
	Account convert(AccountData data) {
		// Param checks
		notNull(data);
		
		// Converting Access to Spring.Security RememberMe format
		Collection<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
		byte access = data.getAccess();
		if(access >= Access.NORMAL.getAccess()) auth.add(new NormalRole());
		if(access >= Access.MODERATOR.getAccess()) auth.add(new ModeratorRole());
		if(access >= Access.ADMIN.getAccess()) auth.add(new AdminRole());
		if(access >= Access.HEADADMIN.getAccess()) auth.add(new HeadAdminRole());
		
		// Return of created account object
		return new Account(data.getId(), data.getUsername(), data.getPassword(), data.getLastLogin(), data.getLastPasswordChange(), data.getExpireOn(), data.getPasswordExpireOn(), data.isEnabled(), data.isLocked(), auth);
	}
	
	/**
	 * Converts {@link Account} back to raw {@link AccountData}
	 * @param acc {@link Account}
	 * @return converted {@link AccountData}
	 */
	AccountData convertBack(Account acc) {
		// Raw data object
		AccountData data = new AccountData();
		
		// Populating data
		data.setId(acc.getId());
		data.setUsername(acc.getName());
		data.setPassword(acc.getPassword());
		data.setAccess(acc.getAccessLevel().getAccess());
		data.setLastLogin(acc.getLastLogin());
		data.setLastPasswordChange(acc.getLastPasswordChange());
		data.setExpireOn(acc.getExpireOn());
		data.setPasswordExpireOn(acc.getPasswordExpireOn());
		data.setEnabled(acc.isEnabled());
		data.setLocked(!acc.isAccountNonLocked());
		
		// Return
		return data;
	}
}
