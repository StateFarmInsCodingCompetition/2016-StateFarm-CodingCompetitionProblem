package com.statefarm.codingcomp.bean;

import java.util.List;
import java.util.Set;

public class Office {
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
}
