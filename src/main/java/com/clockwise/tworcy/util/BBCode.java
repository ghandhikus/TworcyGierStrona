package com.clockwise.tworcy.util;

import javax.annotation.PostConstruct;

import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;
import org.springframework.stereotype.Component;

@Component("bbcode")
public class BBCode {

	private TextProcessor processor;
	
	@PostConstruct
	public void init()
	{
		processor = BBProcessorFactory.getInstance().create();
	}
	
	public String parse(String text) {
		synchronized(processor) { return processor.process(text); }
	}
}
