package com.statefarm.codingcomp.agent;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.statefarm.codingcomp.bean.*;
import com.statefarm.codingcomp.utilities.SFFileReader;

@Component
public class AgentParser { 
	@Autowired
	private SFFileReader sfFileReader;

	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
		// set up agent and read from file
		Agent a = new Agent();
		String s = new SFFileReader().readFile(fileName);
		Document doc = Jsoup.parse(s);
		
		// parse and add the products this agent can produce
		Elements elem = doc.getElementsByAttributeValue("itemprop", "description");
		HashSet<Product> products = new HashSet<Product>();
		for (Element e: elem.select("li")) {
			products.add(Product.fromValue(e.text()));
		}
		a.setProducts(products);
		
		// get the office information
		// primary office
		Office primary = new Office();
		Address primAddress = new Address();
		Elements elem = doc.getElementsByAttributeValue("")
		primAddress.setLine1("first line");
		primAddress.setLine2("second line");
		primAddress.setState(USState.fromValue("state"));
		primAddress.setCity("city");
		primAddress.setPostalCode("postal code");
		primary.setAddress(primAddress);
		
		// secondary office
		Office secondary = new Office();
		Address secAddress = new Address();
		secAddress.setLine1("first line");
		secAddress.setLine2("second line");
		secAddress.setState(USState.fromValue("state"));
		secAddress.setCity("city");
		secAddress.setPostalCode("postal code");
		secondary.setAddress(secAddress);
		
		
		// return finished agent
		return a;
	}
}
