package com.statefarm.codingcomp.bean;

import java.util.List;
import java.util.Set;

public class Agent {
	private String name;

	private Set<Product> products;
	private List<Office> offices;

	public String getName() {
		return name;
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
