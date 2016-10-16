package com.statefarm.codingcomp.bean;

public enum Product {
	AUTO("Auto Insurance"), 
	HOME_AND_PROPERTY("Home and Property Insurance"), 
	LIFE("Life Insurance"), 
	HEALTH("Health Insurance"), 
	BANK("Banking Products"), 
	ANNUITIES("Annuities"), 
	MUTUAL_FUNDS("Mutual Funds");

	private final String value;
	
	private Product(String textValue) {
		value = textValue;
	}
	
	public String getValue() {
		return value;
	}
	
	/**
	 * Lookup method to convert from String to enum value
	 * @param textValue
	 * @return
	 */
	public static Product fromValue(String textValue) {
		for(Product p : values()) {
			if(p.value.equalsIgnoreCase(textValue)) {
				return p;
			}
		}
		return null;
	}
}
