package com.statefarm.codingcomp.bean;

import java.util.List;
import java.util.Set;

public class Agent {
	private String name;
	private String lastName;
	private String suffix;

	private Set<Product> products;
	private List<Office> offices;

	public String getName() {
		return name;
	}

	public String getFirstName() {
		return name.split(" ")[0];
	}

	public String getLastName() {
        String[] splitName = name.split(" ");
        String lastName = null;
        if (splitName.length >= 2) {
            lastName = splitName[1];
        }
        return lastName;
	}

	public String getSuffix() {
        String[] splitName = name.split(" ");
        String suffix = null;
        if (splitName.length >= 3) {
            suffix = splitName[2];
        }
        return suffix;
	}

	public void setName(String firstName) {
		this.name = firstName;
	}

	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}
	
	public List<Office> getOffices() {
		return offices;
	}

	public void setOffices(List<Office> offices) {
		this.offices = offices;
	}	
}
