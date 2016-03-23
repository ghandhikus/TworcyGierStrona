package com.clockwise.tworcy.model.userprofile;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

public @Transactional @Repository class UserProfileRepositoryHibernate implements UserProfileRepository {

	private @Autowired SessionFactory sessionFactory;
	
	private Session getSession(){ return sessionFactory.getCurrentSession(); }
	
	public @Override void addUserProfile(UserProfile userProfile) {
		if(userProfile == null) return;
		getSession().persist(userProfile);
	}

	public @Override UserProfile getUserProfileById(Integer id) {
		if(id == null) return null;
		return (UserProfile) getSession().load(UserProfile.class, id);
	}

	
	public @Override void updateUserProfile(UserProfile userProfile) {
		if(userProfile == null) return;
		getSession().update(userProfile);
	}

	public @Override void removeUserProfile(UserProfile userProfile) {
		if(userProfile == null) return;
		getSession().delete(userProfile);
	}

	public @Override void removeUserProfileById(Integer id) {
		if(id == null) return;
		UserProfile profile = getUserProfileById(id);
		if(profile!=null)
			getSession().delete(profile);
	}

	public @Override List<UserProfile> getUserProfiles(Integer count, Integer offset) {
		if(count == null || count == 0) count = 10;
		if(offset == null) offset = 0;

        Query query = getSession().createQuery("from UserProfile")
        		.setFirstResult(offset)
        		.setMaxResults(count);
        
        @SuppressWarnings("unchecked")
		List<UserProfile> profiles = query.list();
        
        return profiles;
	}

	public @Override UserProfile getUserProfileByAccountId(Integer id) {
		if(id == null) return null;
		Query query = getSession().createQuery("from UserProfile where accountId=:accountId");
		query.setParameter("accountId", id);
		
		return (UserProfile) query.uniqueResult();
	}

}
