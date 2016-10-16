package com.statefarm.codingcomp.bean;

import java.util.HashMap;
import java.util.Map;

public class Languages {

	private static Languages instance = null;
	private static Map<String, String> langMap;
	
	protected Languages() {
		langMap = new HashMap<String, String>();
		buildLangMap();
	}
	
	public static Languages getInstance() {
		if (instance == null) {
			instance = new Languages();
		}
		return instance;
	}
	
	private static void buildLangMap() {
		langMap.put("English", "English");
		langMap.put("Español", "Spanish");
	}
	
	public static String getEnglishLang(String key) {
		System.out.println(key);
		return langMap.get(key);
	}
}
