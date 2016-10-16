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
		boolean additionalExists = false;
		for (Element addr: doc.getElementsByAttributeValue("itemprop", "address")) {
			// determine if main or secondary office
			if (addr.getElementById("locStreetContent_mainLocContent") != null) {
				addressIndex = 0;
			} else if (addr.getElementById("locStreetContent_additionalLocContent_0") != null){
				addressIndex = 1;
				additionalExists = true;
			} else {
				continue;
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
		// get information on office hours 
		ArrayList<String> primOfficeHrs = new ArrayList<String>();
		ArrayList<String> secOfficeHrs = new ArrayList<String>();
		for (Element e: doc.getElementsByAttributeValue("itemprop", "openingHours")) {
			if (e.attr("id").contains("mainLocContent"))
				primOfficeHrs.add(e.text());
			else
				secOfficeHrs.add(e.text());
		}
		offices.get(0).setOfficeHours(primOfficeHrs);
		offices.get(1).setOfficeHours(secOfficeHrs);
		// get information on phones
		boolean primFound = false, secFound=false;
		for (Element e: doc.getElementsByAttributeValue("itemprop", "telephone")) {
			if (e.getElementById("offNumber_mainLocContent") != null) {
				if (!primFound) {
					primFound = true;
					offices.get(0).setPhoneNumber(e.getElementById("offNumber_mainLocContent").getElementsByTag("span").get(1).text());
				}
			} else if (e.getElementById("offNumber_additionalLocContent_0") != null){
				if (!secFound) {
					secFound = true;
					offices.get(1).setPhoneNumber(e.getElementById("offNumber_additionalLocContent_0").getElementsByTag("span").get(1).text());
				}
			}
		}
		// get name
		a.setName(doc.getElementById("AgentName").getElementsByTag("b").text());
		// get languages
		HashSet<String> primLangs = new HashSet<String>();
		HashSet<String> secLangs = new HashSet<String>();
		String idStr;
		for (Element e: doc.getAllElements()) {
			idStr = e.id();
			if (idStr.startsWith("language") && !idStr.startsWith("languageLabel")) {
				if (idStr.contains("mainLocContent")) 
					primLangs.add(idStr.substring(8,idStr.indexOf('_')));
				else if (idStr.contains("additionalLocContent"))
					secLangs.add(idStr.substring(8, idStr.indexOf('_')));
			}				
		}
		offices.get(0).setLanguages(primLangs);
		offices.get(1).setLanguages(secLangs);
		// add in the offices
		for (int i=0; i<2; i++)
			offices.get(i).setAddress(addresses[i]);
		// only the main office exists
		if (!additionalExists)
			offices.remove(1);
		a.setOffices(offices);
		// return finished agent
		return a;
	}
}
