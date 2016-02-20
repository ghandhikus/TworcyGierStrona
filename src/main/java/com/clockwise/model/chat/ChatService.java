package com.clockwise.model.chat;

import org.json.simple.JSONArray;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.clockwise.model.account.Account;



public @Service @Repository interface ChatService {
	public void getMessages(Account account, JSONArray json, Integer lastMessage);
	public void newMessages(Account account, String[] messages);
	public void newMessage(Account account, String msg);
}
