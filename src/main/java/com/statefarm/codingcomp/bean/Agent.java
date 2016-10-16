package com.statefarm.codingcomp.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Element;

import com.statefarm.codingcomp.agent.Parsable;
import com.statefarm.codingcomp.agent.ParserUtils;

public class Agent implements Parsable{
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

	public boolean hasOfficeIn(USState state){
		for(Office o : getOffices()){
			if(o.getAddress().getState().equals(state)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void parse(Element webpage){
		
		//get the name
		//the name will be under the tag with itemprop of "name"
		name = webpage.getElementsByAttributeValue("itemprop", "name").first().text().trim().replace(",", "");
		
		//get the products sold by this agent
		//they will be under the div with an itemprop of "description" under a div titled "Products Offered/Serviced by this Agent"
		Element description = webpage.getElementsByAttributeValue("data-title", "Products Offered/Serviced by this Agent").first().getElementsByAttributeValue("itemprop", "description").first();
		List<String> productNames = ParserUtils.getTextFieldsUnderElement(description);
		
		//convert the strings to Product enums
		products = new HashSet<Product>();
		for(String product : productNames){
			products.add(Product.fromValue(product));
		}
		
		//get the offices
		//the offices will each be a div element under the div under the div under the div with an id of "tabGroupLocation"
		offices = new ArrayList<Office>();
		for(Element office : webpage.getElementById("tabGroupLocation").children().first().children().first().children()){
			if(office.tagName().equals("div")){
				Office o = new Office();
				o.parse(office);
				offices.add(o);
			}
		}
		
		
	}	
}
