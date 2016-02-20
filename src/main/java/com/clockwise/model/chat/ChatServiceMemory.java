package com.clockwise.model.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.clockwise.model.account.Account;

public @Service @Repository class ChatServiceMemory implements ChatService {
	
	private TextProcessor processor;
	
	private List<String> messages = Collections.synchronizedList(new ArrayList<String>());
	
	@PostConstruct
	public void init()
	{
		processor = BBProcessorFactory.getInstance().create();
		messages.add("Welcome!");
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void getMessages(Account account, JSONArray json, Integer lastMessage) {
		if(account == null) return;
		if(lastMessage == null) return;
		
		synchronized(messages)
		{
			// Check if there are new messages are for him
			if(messages.size()-1 > lastMessage)
			{
				int start = lastMessage-1;
				// Limit the chat to minimal and maximal of the array
				if(start>messages.size()) start=messages.size()-1;
				if(start<0) start=0;
				// Iterate through new messages.
				for(int i = start; i < messages.size(); i++)
				{
					// Populate new messages
					JSONObject obj = new JSONObject();
					obj.put("id", i);
					obj.put("msg", messages.get(i));
					json.add(obj);
					System.out.println("Sending object : "+obj.toJSONString());
				}
			}
		}
	}

	@Override
	public void newMessages(Account account, String[] messages) {
		if(messages!=null && messages.length>0)
			for(String msg : messages)
				newMessage(account, msg);
	}

	@Override
	public void newMessage(Account account, String msg) {
		synchronized(processor)
		{
			synchronized(messages)
			{
				msg = "[b]"+account.getName()+"[/b]: "+msg;
				String processed = processor.process(msg);
				messages.add(processed);
				System.out.println("Got new Message : "+processed);
			}
		}
	}

}
