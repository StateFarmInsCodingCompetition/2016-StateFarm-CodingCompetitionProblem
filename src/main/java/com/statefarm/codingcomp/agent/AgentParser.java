package com.statefarm.codingcomp.agent;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.*;

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
	
	private static String removeCommas(String s) {
		String ans="";
		for (char c: s.toCharArray())
			if (c != ',')
				ans += c;
		return ans;
	}

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
		ArrayList<Office> offices = new ArrayList<Office>();
		offices.add(new Office());
		offices.add(new Office());
		
		// get the address information
		Address[] addresses = new Address[2];
		for (int i=0; i<2; i++)
			addresses[i] = new Address();
		int addressIndex;
		for (Element addr: doc.getElementsByAttributeValue("itemprop", "address")) {
			// determine if main or secondary office
			if (addr.getElementById("locStreetContent_mainLocContent") != null) {
				addressIndex = 0;
			} else {
				addressIndex = 1;
			}
			// get address lines 1 and 2
			if (addressIndex == 0) 
				elem = addr.getElementsByAttributeValue("id", "locStreetContent_mainLocContent");
			else
				elem = addr.getElementsByAttributeValue("id", "locStreetContent_additionalLocContent_0");
			String[] L = elem.toString().split("<br>");
			if (L.length == 2) {
				addresses[addressIndex].setLine1(removeCommas(L[0].substring(L[0].lastIndexOf('>')+1).trim()));
				addresses[addressIndex].setLine2(removeCommas(L[1].substring(0, L[1].indexOf('<')).trim()));
			}
			else {
				addresses[addressIndex].setLine1(removeCommas(elem.text().trim()));
				addresses[addressIndex].setLine2(null);
			}
			// get state, city, postal code
			addresses[addressIndex].setState(USState.valueOf(addr.getElementsByAttributeValue("itemprop", "addressRegion").get(0).text().trim()));
			String city = addr.getElementsByAttributeValue("itemprop", "addressLocality").text().trim();
			addresses[addressIndex].setCity(city.substring(0, city.indexOf(',')));
			addresses[addressIndex].setPostalCode(addr.getElementsByAttributeValue("itemprop", "postalCode").text().trim());
		}
		// get information on office hours - in progress
		/*for (Element e: doc.getElementsByAttributeValue("itemprop", "openingHours")) {
			if (e.attr("id").contains("mainLocContent"))
				
		}*/
		//offices.get(0).setOfficeHours
		
		// add in the offices
		for (int i=0; i<2; i++)
			offices.get(i).setAddress(addresses[i]);
		a.setOffices(offices);
		// return finished agent
		return a;
	}
}
