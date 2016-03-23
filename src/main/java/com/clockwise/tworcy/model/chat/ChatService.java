package com.clockwise.tworcy.model.chat;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.clockwise.tworcy.model.account.Account;



public @Service @Repository interface ChatService {
	public void getMessages(Account account, List<String> json, Integer lastMessage);
	public void newMessages(Account account, String[] messages);
	public void newMessage(Account account, String msg);
}
