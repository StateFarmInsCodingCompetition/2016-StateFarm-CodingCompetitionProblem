package com.statefarm.codingcomp.bean;

import java.util.List;

import org.jsoup.nodes.Element;

import com.statefarm.codingcomp.agent.Parsable;
import com.statefarm.codingcomp.agent.ParserUtils;

public class Address implements Parsable{
	private String line1;
	private String line2;
	private String city;
	private USState state;
	private String postalCode;

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public USState getState() {
		return state;
	}

	public void setState(USState state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Override
	public void parse(Element element){
		List<String> addressLines = ParserUtils.getTextFieldsUnderNode(element.getElementsByAttributeValue("itemprop", "streetAddress").first());
		setLine1(addressLines.get(0).trim().replace(",", ""));
		if(addressLines.size()>=3) setLine2(addressLines.get(2).trim().replace(",", ""));
		
		setCity(ParserUtils.getTextFieldsUnderElementWithItemprop("addressLocality", element).get(0).replace(",", ""));
		setState(USState.valueOf(ParserUtils.getTextFieldsUnderElementWithItemprop("addressRegion", element).get(0).trim()));
		setPostalCode(ParserUtils.getTextFieldsUnderElementWithItemprop("postalCode", element).get(0).trim());
		
		
	}
	
	
}
