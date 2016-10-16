package com.statefarm.codingcomp.agent;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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
	//comment
	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
		String s = sfFileReader.readFile(fileName);
		Agent agent = new Agent();
		//System.out.print(s);
		//System.out.print(fileName);
		Document doc = Jsoup.parse(s);
		Elements elems = doc.getElementsByAttributeValue("itemprop", "description");
		Element productList = elems.get(0);
		Elements products = productList.getElementsByTag("li");
		

		Set<Product> prodSet = new HashSet<Product>();
		for (int i = 0; i < products.size(); i++) {
			prodSet.add(Product.fromValue(products.get(i).text()));
		}
		agent.setProducts(prodSet);
		
		String address = doc.getElementById("locStreetContent_mainLocContent").text();
		
		
		Office office = new Office();
		office.setAddress(address);
		
		
		//agent.setOffices(offices);

		return agent;
	}
}
