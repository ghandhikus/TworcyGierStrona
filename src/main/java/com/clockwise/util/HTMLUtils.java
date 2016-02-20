package com.clockwise.util;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

public @Component("htmlUtils") class HTMLUtils {


	/**
	 * Splits the string on given position. Fixes or removes the tags which are broken after splitting.
	 * @see Jsoup#parse
	 * @param str
	 * @param splitPosition
	 * @return
	 */
	public String split(String string, int splitPosition) {
		if (string == null) return string;
		if (string.length() <= 2) return string;
		if (splitPosition == 0) return "";
		
		// Splits the string and fixes the tags
		String ret = Jsoup.parse(string.substring(0, splitPosition)).body().html();
		
		ret = ret.trim();
		splitPosition = ret.length();

		
		// Fixes end of string, removes spaces brs
		for(int i=splitPosition;i>=0;i--)
		{
			// Removes < which jsoup does replace to html tag
			if(combinationBefore(i,ret,"<")) {splitPosition-=1; continue;}
			if(combinationBefore(i,ret,"&lt;")) {splitPosition -=4; continue;}
			
			// Space
			if(combinationBefore(i,ret," ")) {splitPosition-=1; continue;}
			
			// <br>
			if(combinationBefore(i,ret,"<br />")) {splitPosition -=6; continue;}
			if(combinationBefore(i,ret,"<br/>")) {splitPosition -=5; continue;}
			if(combinationBefore(i,ret,"<br>")) {splitPosition -=4; continue;}
			
			// Every check was okay, break the loop.
			if(i==splitPosition) break;
		}
		
		return ret.substring(0, splitPosition);
	}
	
	/**
	 * Checks if text contains combination before i.
	 * @param i position to check
	 * @param text 
	 * @param combination
	 * @return
	 */
	public boolean combinationBefore(int i, String text, String combination) {
		if(i-combination.length()<0) return false;
		String sequence = (String) text.subSequence(i-combination.length(), i);
		return (sequence.toLowerCase().equals(combination.toLowerCase()));
	}
	
	/**
	 * Splits the string on given position. If string after splitting 
	 * @param str
	 * @param splitPosition
	 * @return
	 *
	public String split(String str, int splitPosition) {
		if (str == null) return str;
		if (str.length() <= 2) return str;
		if (splitPosition == 0) return "err1";

		// Limit operation
		//System.out.println("ITERATIONS : "+iterations);
		for (int i = 0; i < 10; i++) {
			// Cutted string
			String split = str.substring(0, splitPosition);
			
			// Checks if html is valid after splitting
			boolean validHTML = tagsClosed.matcher(split).matches();
			//System.out.printf("split(%s) isValid(%s)\n", split, validHTML);
			
			// Moves it to last tag opening until html is fixed.
			if (!validHTML)
				splitPosition = split.lastIndexOf("<");

			// String html is broken if splitposition reaches 0 or less
			if (splitPosition <= 0)
				return "err2";

			// There is no need to move and string is valid html
			if (validHTML) return split;
		}

		// String was broken and shouldn't be shown
		return "err3";
	}*/

	//private Pattern tagsClosed = Pattern.compile("^(?:<(\\w+)(?:(?:\\s+\\w+(?:\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)>[^<>]*<\\/\\1+\\s*>|<\\w+(?:(?:\\s+\\w+(?:\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)\\/>|<!--.*?-->|[^<>]+)*$");
}
