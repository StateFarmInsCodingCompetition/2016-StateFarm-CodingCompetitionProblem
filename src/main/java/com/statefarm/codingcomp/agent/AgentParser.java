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

import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.bean.Product;
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
		
		// return finished agent
		return a;
	}
}
