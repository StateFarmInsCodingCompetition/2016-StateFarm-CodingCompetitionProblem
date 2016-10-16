package com.statefarm.codingcomp.agent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.statefarm.codingcomp.bean.Address;
import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.bean.Languages;
import com.statefarm.codingcomp.bean.Office;
import com.statefarm.codingcomp.bean.Product;
import com.statefarm.codingcomp.bean.USState;
import com.statefarm.codingcomp.utilities.SFFileReader;

@Component
public class AgentParser {
	@Autowired
	private SFFileReader sfFileReader;
	private final String DELIMITER = "<br>";

	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
		// Singleton instance of Languages to convert native language names to English language names
		Languages.getInstance();
		
		Agent agent = new Agent();
		File input = new File(fileName);
		
		try {
			Document doc = Jsoup.parse(input, "UTF-8", "");
			agent.setName(doc.select("span[itemprop$=name]").text());
			
			// Set agent products
			Set<Product> products = new HashSet<Product>();
			Elements productElements = doc.select("div[itemprop$=description] > ul > li");
			for(Element e : productElements) {
				products.add(Product.fromValue(e.text()));
			}
			agent.setProducts(products);
			
			Office office = new Office();
			
			// Set office address
			Address address = new Address();
			//Street for main location
			String[] addrLines = doc.select("span[id=locStreetContent_mainLocContent]").html().replace(",", "").split(DELIMITER);
			if (addrLines.length == 1) {
				address.setLine1(addrLines[0].trim());
			} else if (addrLines.length >= 2) {
				address.setLine1(addrLines[0].trim());
				address.setLine2(addrLines[1].trim());
			} 
			//City for main Location
			address.setCity(doc.select("span[itemprop=addressLocality]").text().split(",")[0].trim());
			//State for first location
			address.setState(USState.fromKey(doc.select("span[itemprop=addressRegion]").text().split(" ")[0]));
			//Postal code main location
			address.setPostalCode(doc.select("span[itemprop=postalCode]").text().split(" ")[0]);
			office.setAddress(address);

			// Set office languages
			Set<String> languages = new HashSet<String>();
			Elements langElements = doc.select("div[id$=panelmainLocation] > div.span5 > ul > li");
			for (Element e : langElements) {
				languages.add(Languages.getEnglishLang(e.text()));
			}
			office.setLanguages(languages);
			
			// Set office phone 
			office.setPhoneNumber(doc.select("span[id$=offNumber_mainLocContent]").text().substring(14, 26));
			
			// Set office hours
			List<String> officeHours = new ArrayList<String>();
			int hoursIterator = 0;
			String hoursSelectString = "span[id=officeHoursContent_mainLocContent_" + hoursIterator + "]";
			while (!StringUtils.isEmpty(doc.select(hoursSelectString).text())) {
				officeHours.add(doc.select(hoursSelectString).text());
				hoursSelectString = "span[id=officeHoursContent_mainLocContent_" + ++hoursIterator + "]";
			}
			office.setOfficeHours(officeHours);
			
			// Assign office to agent
			List<Office> offices = new ArrayList<Office>();
			offices.add(office);
						
			// Set secondary office if it exists... pardon bad variable names
			if (!StringUtils.isEmpty(doc.select("span[id=locStreetContent_additionalLocContent_0]").html())) {
				Office secondary = new Office();
				Address address2 = new Address();
				
				//Street for second location
				String[] addrLines2 = doc.select("span[id=locStreetContent_additionalLocContent_0]").html().replace(",", "").split(DELIMITER);
				if (addrLines2.length == 1) {
					address2.setLine1(addrLines2[0].trim());
				} else if (addrLines2.length >= 2) {
					address2.setLine1(addrLines2[0].trim());
					address2.setLine2(addrLines2[1].trim());
				}
				//City for second location
				address2.setCity(doc.select("span[itemprop=addressLocality]").text().split(",")[1].trim());
				//State for second location
				address2.setState(USState.fromKey(doc.select("span[itemprop=addressRegion]").text().split(" ")[1]));
				//Postal code secondary location
				address2.setPostalCode(doc.select("span[itemprop=postalCode]").text().split(" ")[1]);
				secondary.setAddress(address2);

				// Set office languages
				Set<String> languages2 = new HashSet<String>();
				Elements langElements2 = doc.select("div[id$=panelmainLocation] > div.span5 > ul > li");
				for (Element e : langElements2) {
					languages2.add(Languages.getEnglishLang(e.text()));
				}
				secondary.setLanguages(languages2);
				
				// Set office phone 
				secondary.setPhoneNumber(doc.select("span[id$=offNumber_mainLocContent]").text().substring(14, 26));
				
				// Set office hours
				List<String> officeHours2 = new ArrayList<String>();
				hoursIterator = 0;
				hoursSelectString = "span[id=officeHoursContent_additionalLocContent_0_" + hoursIterator + "]";
				while (!StringUtils.isEmpty(doc.select(hoursSelectString).text())) {
					officeHours2.add(doc.select(hoursSelectString).text());
					hoursSelectString = "span[id=officeHoursContent_additionalLocContent_0_" + ++hoursIterator + "]";
				}
				secondary.setOfficeHours(officeHours2);
				
				offices.add(secondary);
			}
			
			// Set agent's about
			List<String> abouts = new ArrayList<String>();
			Elements aboutElements = doc.select("div[id=aboutMeContent] > ul > li");
			for (Element e : aboutElements) {
				abouts.add(e.text());
			}
			agent.setAbouts(abouts);
			
			// Assign offices to agent
			agent.setOffices(offices);
			
		} catch (IOException e) {
			System.out.println("HTML file for agent could not be read!");
			e.printStackTrace();
		}
		
		return agent;
	}
}
