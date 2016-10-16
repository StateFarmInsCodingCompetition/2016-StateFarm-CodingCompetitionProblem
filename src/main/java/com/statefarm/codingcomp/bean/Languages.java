package com.statefarm.codingcomp.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Languages singleton
 *
 */
public class Languages {

	private static Languages instance = null;
	private static Map<String, String> langMap;
	
	/**
	 * Prevent instantiation
	 */
	protected Languages() {
		langMap = new HashMap<String, String>();
		buildLangMap();
	}
	
	
	/**
	 * Allow an instance to be created or referenced
	 */
	public static Languages getInstance() {
		if (instance == null) {
			instance = new Languages();
		}
		return instance;
	}
	
	/**
	 * Build map based on languages which appear on agent pages
	 */
	private static void buildLangMap() {
		langMap.put("English", "English");
		langMap.put("Español", "Spanish");
		langMap.put("Deutsch", "German");
	}
	
	/**
	 * Convert from native language name to English language name
	 * @param key native langauge name
	 * @return English language name
	 */
	public static String getEnglishLang(String key) {
		return langMap.get(key);
	}
}
