package com.clockwise.controllers;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clockwise.model.account.Account;
import com.clockwise.model.chat.ChatService;

@Controller
@RequestMapping("/chat")
public class Chat {

	@Autowired
	ChatService chat;
	
	@RequestMapping("/ajax")
	@SuppressWarnings("unchecked")
	public @ResponseBody String ajax(Integer lastMessage, HttpServletRequest request)
	{
		String[] messages = request.getParameterMap().get("messages[]");
		
		/*Enumeration<String> params = request.getParameterNames();
		while(params.hasMoreElements())
			System.out.println("Param : "+params.nextElement());
		*/
		
		Account account = (Account) request.getSession().getAttribute("account");
		JSONArray json = new JSONArray();
		
		
		if(account == null)
		{
			json.add("notLogged");
		}
		// Check if he's logged in.
		else if(account != null)
		{
			if(lastMessage == null)
			{
				json.add("error");
				return json.toJSONString();
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
		return json.toJSONString();
	}
}
