package com.clockwise.tworcy.model.userprofile;

import java.security.AccessControlException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clockwise.tworcy.model.account.Account;

public @Service interface UserProfileService {
	void addUserProfile(Account by, UserProfile userProfile) throws AccessControlException;
	UserProfile getUserProfile(Integer id);
	List<UserProfile> getUserProfiles(Integer count, Integer offset);
	void updateUserProfile(Account by, UserProfile userProfile) throws AccessControlException;
	void removeUserProfile(Account by, Integer id) throws AccessControlException;
	void removeUserProfile(Account by, UserProfile userProfile) throws AccessControlException;
	UserProfile getUserProfileByAccount(Account account);
	UserProfile getUserProfileByAccountId(Integer accountId);
	boolean canEdit(Account account, UserProfile userProfile) throws AccessControlException;
	boolean canRemove(Account account, UserProfile userProfile) throws AccessControlException;
	void createUserProfile(Account account);
}
