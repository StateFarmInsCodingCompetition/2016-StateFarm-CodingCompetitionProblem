package com.statefarm.codingcomp.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Element;

import com.statefarm.codingcomp.agent.Parsable;
import com.statefarm.codingcomp.agent.ParserUtils;

public class Office implements Parsable{
	private Set<String> languages;
	private String phoneNumber;
	private List<String> officeHours;
	private Address streetAddress;

	public Set<String> getLanguages() {
		return languages;
	}

	public void setLanguages(Set<String> languages) {
		this.languages = languages;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<String> getOfficeHours() {
		return officeHours;
	}

	public void setOfficeHours(List<String> officeHours) {
		this.officeHours = officeHours;
	}

	public Address getAddress() {
		return streetAddress;
	}

	public void setAddress(Address address) {
		this.streetAddress = address;
	}

	@Override
	public void parse(Element element){
		//phone number, office hours, and street address are all under tags with specific attributes
		phoneNumber = ParserUtils.getTextFieldsUnderElement(element.getElementsByAttributeValue("itemprop", "telephone").first()).get(1).trim();
		officeHours = ParserUtils.getTextFieldsUnderElements(element.getElementsByAttributeValue("itemprop", "openingHours"));
		streetAddress = new Address();
		streetAddress.parse(element.getElementsByAttributeValue("itemprop", "address").first());
		
		//the languages are a bit more complicated and are an attribute under a tag with specific ids
		this.languages = new HashSet<String>();
		for(Element e : element.getElementsByAttributeValueStarting("id", "language")){
			if(e.tagName().equals("div")){
				languages.add(e.child(0).child(0).attr("title"));
			}
		}
		
	}
	
}
