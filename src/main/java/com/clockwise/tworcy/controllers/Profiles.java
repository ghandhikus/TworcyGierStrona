package com.clockwise.tworcy.controllers;

import java.security.AccessControlException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.clockwise.tworcy.exception.ParameterTooLongException;
import com.clockwise.tworcy.model.account.Account;
import com.clockwise.tworcy.model.account.AccountService;
import com.clockwise.tworcy.model.userprofile.UserProfile;
import com.clockwise.tworcy.model.userprofile.UserProfileService;

@Controller
@RequestMapping(value = "/profile")
public class Profiles {

	private @Autowired AccountService accounts;
	private @Autowired UserProfileService profiles;

	@RequestMapping("/")
	public ModelAndView home() {
		Account acc = accounts.getLogged();

		if (acc == null)
			return new ModelAndView("redirect:/account/login?ret=/profile/");

		// Logged in, show page.
		ModelAndView ret = new ModelAndView("core");
		ret.addObject("account", acc);
		ret.addObject("profile", profiles.getUserProfileByAccount(acc));
		return ret;
	}

	@RequestMapping("/get/{accountId}")
	public ModelAndView getProfile(
			@PathVariable(value = "accountId") Integer accountId) {
		Account account = accounts.getLogged();
		UserProfile profile = profiles.getUserProfileByAccountId(accountId);
		ModelAndView ret = new ModelAndView("core");
		ret.addObject("account", account);
		ret.addObject("profile", profile);

		return ret;
	}
	@RequestMapping("/remove/{accountId}")
	public ModelAndView removeProfile(
			@PathVariable(value = "accountId") Integer accountId) {
		Account account = accounts.getLogged();
		if (account == null)
			return new ModelAndView(
					"redirect:/account/login?ret=/profile/edit/" + accountId);

		try {
			UserProfile profile = profiles.getUserProfileByAccountId(accountId);
			if (profiles.canRemove(account, profile) && profile != null) {
				profiles.removeUserProfile(account, profile);
				return new ModelAndView("redirect:/profile/");
			}
		} catch (NullPointerException | AccessControlException
				| ParameterTooLongException e) {
			ModelAndView ret = new ModelAndView("core");
			ret.addObject("account", account);
			ret.addObject("error", e.getMessage());

			return ret;
		}
		
		return new ModelAndView("redirect:/");
	}
	@RequestMapping("/edit/{accountId}")
	public ModelAndView editProfile(
			@PathVariable(value = "accountId") Integer accountId) {
		Account account = accounts.getLogged();
		if (account == null)
			return new ModelAndView(
					"redirect:/account/login?ret=/profile/edit/" + accountId);
		// TODO: Load cache data from previous form POST, after redirecting here

		try {
			UserProfile profile = profiles.getUserProfileByAccountId(accountId);

			// Allow editing of the permitted profile and create a new one if
			// null
			if (profiles.canEdit(account, profile) || profile == null) {
				ModelAndView ret = new ModelAndView("core");
				ret.addObject("account", account);
				ret.addObject("profile", profile);

				return ret;
			}
		} catch (NullPointerException | AccessControlException
				| ParameterTooLongException e) {
			ModelAndView ret = new ModelAndView("core");
			ret.addObject("account", account);
			ret.addObject("error", e.getMessage());

			return ret;
		}

		return new ModelAndView("redirect:/");
	}

	@RequestMapping(value = "/edit/{accountId}", method = RequestMethod.POST)
	public ModelAndView editProfile(
			@PathVariable(value = "accountId") Integer accountId,
			String description) {
		// Catch account
		Account account = accounts.getLogged();
		// TODO: Hold data before redirecting to login
		if (account == null)
			return new ModelAndView(
					"redirect:/account/login?ret=/profile/edit/" + accountId);

		try {
			// Get profile
			UserProfile profile = profiles.getUserProfileByAccountId(accountId);
			// Check permissions
			if (profiles.canEdit(account, profile)) {
				// Do not create profile for someone else
				if (profile == null && account.getId()!=accountId)
					return new ModelAndView("redirect:/profile/");
				// Create if does not exist
				if (profile == null) {
					profiles.createUserProfile(account);
					profile = profiles.getUserProfileByAccountId(accountId);
				}
				// Update
				profile.setDescription(description);
				profiles.updateUserProfile(account, profile);
			}
			// Return to the profile
			return new ModelAndView("redirect:/profile/get/" + accountId);
		} catch (AccessControlException | NullPointerException
				| ParameterTooLongException e) {
			ModelAndView ret = new ModelAndView("core");
			ret.addObject("account", account);
			ret.addObject("errors", e.getMessage());

			return ret;
		}
	}
}
