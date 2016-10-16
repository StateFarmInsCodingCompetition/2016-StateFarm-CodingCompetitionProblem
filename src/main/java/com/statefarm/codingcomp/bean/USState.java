package com.statefarm.codingcomp.bean;

public enum USState {
	
	AL("Alabama"),
	AK("Alaska"),
	AZ("Arizona"),
	AR("Arkansas"),
	CA("California"),
	CO("Colorado"),
	CT("Connecticut"),
	DE("Delaware"),
	FL("Florida"),
	GA("Georgia"),
	HI("Hawaii"),
	ID("Idaho"),
	IL("Illinois"),
	IN("Indiana"),
	IA("Iowa"),
	KS("Kansas"),
	KY("Kentucky"),
	LA("Louisiana"),
	ME("Maine"),
	MD("Maryland"),
	MA("Massachusetts"),
	MI("Michigan"),
	MN("Minnesota"),
	MS("Mississippi"),
	MO("Missouri"),
	MT("Montana"),
	NE("Nebraska"),
	NV("Nevada"),
	NH("New Hampshire"),
	NJ("New Jersey"),
	NM("New Mexico"),
	NY("New York"),
	NC("North Carolina"),
	ND("North Dakota"),
	OH("Ohio"),
	OK("Oklahoma"),
	OR("Oregon"),
	PA("Pennsylvania"),
	RI("Rhode Island"),
	SC("South Carolina"),
	SD("South Dakota"),
	TN("Tennessee"),
	TX("Texas"),
	UT("Utah"),
	VT(" Vermont"),
	VA("Virginia"),
	WA("Washington"),
	DC("Washington D.C."),
	WV("West Virginia"),
	WI("Wisconsin"),
	WY("Wyoming");
	
	private final String value;
	
	private USState(String textValue) {
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
	public static USState fromValue(String textValue) {
		for(USState s : values()) {
			if(s.value.equalsIgnoreCase(textValue)) {
				return s;
			}
		}
		return null;
	}
}