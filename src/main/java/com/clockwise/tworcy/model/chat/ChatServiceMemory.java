package com.clockwise.tworcy.model.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.clockwise.tworcy.model.account.Account;
import com.google.gson.Gson;

public @Service @Repository class ChatServiceMemory implements ChatService {

    private @Autowired ServletContext servletContext;
    
	private TextProcessor processor;
	
	private List<String> messages = Collections.synchronizedList(new ArrayList<String>());
	private final Gson gson = new Gson();
	
	private static final Logger logger = Logger.getLogger(ChatServiceMemory.class);
	
	@PostConstruct
	public void init()
	{
		processor = BBProcessorFactory.getInstance().create();
		messages.add("null");
	}
	
	@Override
	public void getMessages(Account account, List<String> json, Integer lastMessage) {
		if(account == null) return;
		if(lastMessage == null) return;

		synchronized(messages)
		{
			// Check if there are new messages are for him
			if(messages.size()-1 > lastMessage)
			{
				int start = lastMessage-1;
				// Limit the chat to minimal and maximal of the array
				if(start > messages.size()) start = messages.size() - 1;
				if(start < 0) start = 0;
				// Iterate through new messages.
				for(int i = start; i < messages.size(); i++)
				{
					// Populate new messages
					Map<String, String> obj = new HashMap<>();
					obj.put("id", ""+i);
					obj.put("msg", messages.get(i));
					String j = gson.toJson(obj);
					json.add(j);
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
				logger.info(account.getName()+": "+msg);
				
				msg = "[url=" + servletContext.getContextPath() + "/profile/get/" +
						account.getId()+"][b]" +account.getName() + "[/b][/url]: " + msg;
				
				String processed = processor.process(msg);
				messages.add(processed);
			}
		}
	}

}
