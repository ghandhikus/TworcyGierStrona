package com.clockwise.tworcy.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clockwise.tworcy.model.account.Account;
import com.clockwise.tworcy.model.chat.ChatService;
import com.google.gson.Gson;

@Controller
@RequestMapping("/chat")
public class Chat {

	private @Autowired ChatService chat;
	private final Gson gson = new Gson();
	
	@RequestMapping("/ajax")
	public @ResponseBody String ajax(Integer lastMessage, HttpServletRequest request)
	{
		String[] messages = request.getParameterMap().get("messages[]");
		
		Account account = (Account) request.getSession().getAttribute("account");

		List<String> json = new ArrayList<>();
		
		if(account == null)
			json.add("notLogged");
		
		// Check if he's logged in.
		else if(account != null)
		{
			if(lastMessage == null)
			{
				json.add("error");
				return gson.toJson(json);
			}
			else
			{
				// Check his messages
				chat.newMessages(account, messages);
				// Prepare new messages.
				chat.getMessages(account, json, lastMessage);
			}
		}
		
		// Send result
		String res = gson.toJson(json);
		return res;
	}
}
