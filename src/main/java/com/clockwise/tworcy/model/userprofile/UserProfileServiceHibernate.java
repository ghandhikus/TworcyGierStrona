package com.clockwise.tworcy.model.userprofile;

import java.security.AccessControlException;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clockwise.tworcy.model.account.Access;
import com.clockwise.tworcy.model.account.Account;

public @Service class UserProfileServiceHibernate implements UserProfileService {

	private @Autowired UserProfileRepository db;

	public @Override @Transactional void addUserProfile(Account by,
			UserProfile userProfile) throws AccessControlException {
		if (canEdit(by, userProfile))
			db.addUserProfile(userProfile);
		else
			throw new AccessControlException("Can't edit");

	}

	public @Override @Transactional UserProfile getUserProfile(Integer id) {
		return db.getUserProfileById(id);
	}

	public @Override @Transactional List<UserProfile> getUserProfiles(
			Integer count, Integer offset) {
		return db.getUserProfiles(count, offset);
	}

	public @Override @Transactional void updateUserProfile(Account by,
			UserProfile userProfile) throws AccessControlException {
		if (canEdit(by, userProfile))
			db.updateUserProfile(userProfile);
		else
			throw new AccessControlException("Can't edit");
	}

	public @Override @Transactional void removeUserProfile(Account by,
			Integer id) throws AccessControlException {
		if (by.getAccess() > Access.NORMAL.getAccess())
			db.removeUserProfileById(id);
		else
			throw new AccessControlException("Can't remove profile");
	}

	public @Override @Transactional void removeUserProfile(Account by,
			UserProfile userProfile) throws AccessControlException {
		if (by.getAccess() > Access.NORMAL.getAccess())
			db.removeUserProfile(userProfile);
		else
			throw new AccessControlException("Can't remove profile");
	}

	public @Override @Transactional UserProfile getUserProfileByAccount(
			Account account) {
		if(account == null) return null;
		if(account.getId() == null) return null;
		return db.getUserProfileByAccountId(account.getId());
	}

	public @Override UserProfile getUserProfileByAccountId(Integer accountId) {
		if(accountId == null) return null;
		return db.getUserProfileByAccountId(accountId);
	}

	public @Override boolean canEdit(Account account, UserProfile userProfile) throws AccessControlException {
		if(account == null) return false;
		if(userProfile == null) return true;
		boolean ret = account.getId() == userProfile.getAccountId()
				|| account.getAccess() > Access.NORMAL.getAccess();
		if(ret == false) throw new AccessControlException("Can't edit");
		return ret;
	}
	public @Override boolean canRemove(Account account, UserProfile userProfile) throws AccessControlException {
		if(account == null) return false;
		if(userProfile == null) return false;
		boolean ret = account.getId() == userProfile.getAccountId()
				|| account.getAccess() > Access.NORMAL.getAccess();
		if(ret == false) throw new AccessControlException("Can't remove");
		return ret;
	}

	public @Override void createUserProfile(Account account) {
		UserProfile profile = new UserProfile();
		profile.setAccountId(account.getId());
		db.addUserProfile(profile);
	}

}
