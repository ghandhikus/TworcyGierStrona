package com.clockwise.tworcy.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;

public @Component("json") class JSONUtils {
	
	private final Gson gson = new Gson();
	
	public String stringArrayToJSON(List<String> images) {
		if(images == null) {
			return null;
		}
		
		String json = gson.toJson(images);

		return json;
	}
	
	public String[] stringArrayFromJSON(String json) {
		return gson.fromJson(json, String[].class);
	}

	public List<String> stringListFromJSON(String json) {
		String[] arr = stringArrayFromJSON(json);
		List<String> list = new ArrayList<String>();
		for(String str : arr) list.add(str);
		return list;
	}
}
