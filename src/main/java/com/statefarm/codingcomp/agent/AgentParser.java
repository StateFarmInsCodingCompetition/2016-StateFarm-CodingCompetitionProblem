package com.statefarm.codingcomp.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.bean.Product;
import com.statefarm.codingcomp.bean.USState;
import com.statefarm.codingcomp.bean.Office;
import com.statefarm.codingcomp.bean.Address;
import com.statefarm.codingcomp.utilities.SFFileReader;

import java.util.*;
import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Component
public class AgentParser {
	private final String SF_BASE_URI = "http://www.statefarm.com";
	@Autowired
	private SFFileReader sfFileReader;
	
	private final String AGENT_NAME_SELECTOR = "#AgentNameLabelId > span:nth-child(1)";
	private final String AGENT_PRODUCT_SELECTOR = "#sfx_defaultToggle_div > div > ul > li";
	// One div for each office
	private final String AGENT_OFFICE_SELECTOR = "#tabGroupOffice > div";

	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
		Agent agent = new Agent();
		try {
			Document agentDoc = Jsoup.parse(new File(fileName), "UTF-8", SF_BASE_URI);

			// This is guaranteed to be a single Element
			Elements agentNameNode = agentDoc.select(AGENT_NAME_SELECTOR);
			Elements agentProductNodes = agentDoc.select(AGENT_PRODUCT_SELECTOR);
			Elements agentOfficeNodes = agentDoc.select(AGENT_OFFICE_SELECTOR);

			Set<Product> agentProducts = new HashSet<Product>();
			Set<String> agentLangs = new HashSet<String>();
			List<Office> agentOffices = new ArrayList<Office>();
			
			Iterator<Element> iter;
			
			// Parse agent products 
			iter = agentProductNodes.iterator();
			while(iter.hasNext()) {
				Element e = iter.next();
				agentProducts.add(Product.fromValue(e.text()));
			}
			
			// Parse agent office data
			iter = agentOfficeNodes.iterator();
			while(iter.hasNext()) {
				Element e = iter.next();
				Address addr = new Address();
				Set<String> languages = new HashSet<String>();
				List<String> officeHours = new ArrayList<String>();
				String phoneNumber;
				Office o = new Office();
				
				String[] address = agentDoc.select(
					"#tabGroupOffice > div > div:nth-child(1) > div[itemprop=address] > div[itemprop=streetAddress] > span"
				).text().split("<br>");
				
				Elements query = e.select("div:nth-child(1) > div[itemprop=address] > div:nth-child(2) > span");
				
				String city = query.select("span[itemprop=addressLocality]").text();
				String state = query.select("span[itemprop=addressRegion]").text();
				String postalCode = query.select("span[itemprop=postalCode]").text();
				
				// Address for this location
				addr.setLine1(address[0].trim());
				addr.setLine2(null);
				if(address.length > 1) {
					addr.setLine2(address[1].trim());
				}
				addr.setCity(city.substring(0, city.length() - 2).trim());
				addr.setState(USState.fromValue(state));
				addr.setPostalCode(postalCode);
				
				// Contact information
				phoneNumber = e.select(
					"div:nth-child(2) > div:nth-child(1) > div[itemprop=telephone] > span > span"
				).text();
				
				// Office Hours
				query = e.select("div:nth-child(2) > div:nth-child(3) > div > span[itemprop=openingHours]");
				Iterator<Element> qiter = query.iterator(); 
				while(qiter.hasNext()) {
					Element q = qiter.next();
					officeHours.add(q.text());
				}
				
				// Languages
				query = e.select("div:nth-child(2) > ul:nth-child(1) > li");
				qiter = query.iterator();
				while(qiter.hasNext()) {
					Element q = qiter.next();
					languages.add(q.text());
				}
				
				o.setAddress(addr);
				o.setLanguages(languages);
				o.setPhoneNumber(phoneNumber);
				o.setOfficeHours(officeHours);
				
				// Add it to the list
				agentOffices.add(o);
			}
			
			
			
			agent.setName(agentNameNode.text());
			agent.setProducts(agentProducts);
			agent.setOffices(agentOffices);
		} catch(IOException e) {
			System.err.println("Cannot find file: " + fileName);
		}
		return agent;
	}
}
