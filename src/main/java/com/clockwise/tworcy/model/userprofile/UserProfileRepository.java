package com.clockwise.tworcy.model.userprofile;

import java.util.List;

import org.springframework.stereotype.Repository;

public @Repository interface UserProfileRepository {
	void addUserProfile(UserProfile userProfile);
	UserProfile getUserProfileById(Integer id);
	void updateUserProfile(UserProfile userProfile);
	void removeUserProfile(UserProfile userProfile);
	void removeUserProfileById(Integer id);
	List<UserProfile> getUserProfiles(Integer count, Integer offset);
	UserProfile getUserProfileByAccountId(Integer id);
}
