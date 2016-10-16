package com.statefarm.codingcomp.agent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.statefarm.codingcomp.bean.Address;
import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.bean.Office;
import com.statefarm.codingcomp.bean.Product;
import com.statefarm.codingcomp.bean.USState;
import com.statefarm.codingcomp.utilities.SFFileReader;

@Component
public class AgentParser {
	@Autowired
	private SFFileReader sfFileReader;
	

	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
		Agent localAgent = new Agent();
		Set<Product> products = new HashSet<Product>();
		products.addAll(Arrays.asList(Product.values()));
		localAgent.setProducts(products);
		
		 
		// New up offices (and addresses for offices) and populate
		Office office1 = new Office();
		Office office2 = new Office();
		Set<String> languages1 = new HashSet<String>();
		Set<String> languages2 = new HashSet<String>();
		List<String> office1Hours = new ArrayList<String>();
		List<String> office2Hours = new ArrayList<String>();
		Address address1 = new Address();
		Address address2 = new Address();
		try {
			File input = new File(fileName);
			Document website = Jsoup.parse(input, "UTF-8");
			localAgent.setName(website.getElementsByAttributeValue("itemprop", "name").text());
			Element agentInfoContent = website.getElementById("agentInfoContent");
			Element span6 = agentInfoContent.getElementsByClass("span6").get(0);
			try{
				//First office Elements
				Element mainOffice = span6.getElementById("tabGroupOffice");
				Element mainAddress = mainOffice.getElementsByClass("span5").get(0);
				Element addressInfo = mainAddress.getElementsByAttributeValue("itemprop", "address").get(0);
				Element mainPhonesandHours = mainOffice.getElementsByClass("span5").get(1);
				Elements mainPhones = mainPhonesandHours.getElementsByAttributeValue("itemprop", "telephone");
				Elements mainHours = website.getElementsByAttributeValueStarting("id", "officeHoursContent_mainLocContent_");
			
				languages1.add(mainOffice.getElementsByTag("li").get(0).getElementsByAttribute("Title").attr("Title"));
				languages1.add(mainOffice.getElementsByTag("li").get(1).getElementsByAttribute("Title").attr("Title"));
				office1.setPhoneNumber(mainPhones.get(0).text().replaceAll("Office Phone: ", ""));
				address1.setCity(addressInfo.getElementsByAttributeValue("itemprop", "addressLocality").text().replace(",",""));
				address1.setPostalCode(addressInfo.getElementsByAttributeValue("itemprop", "postalCode").text());
				address1.setState(USState.valueOf(addressInfo.getElementsByAttributeValue("itemprop", "addressRegion").text()));
				address1.setLine1(addressInfo.getElementById("locStreetContent_mainLocContent").text());
				if (address1.getLine1().contains("Ste")){
					String Line1 = address1.getLine1().split(" Ste")[0];
					String Line2 = "Ste"+address1.getLine1().split("Ste")[1];
					address1.setLine1(Line1);
					address1.setLine2(Line2);
				}
				for (Element element : mainHours){office1Hours.add(element.text());}
				
				}
				
			finally{}
			Element Office2 = website.getElementById("paneladditionalLoc_0");
			if (Office2 != null) {
				Element mainOffice2 = Office2.getElementsByClass("span5").get(0);
				Element mainAddress2 = Office2.getElementsByClass("span5").get(1);
				languages2.add(Office2.getElementsByTag("li").get(0).getElementsByAttribute("Title").attr("Title"));
				languages2.add(Office2.getElementsByTag("li").get(1).getElementsByAttribute("Title").attr("Title"));
				office2.setPhoneNumber(mainAddress2.getElementsByAttributeValue("itemprop", "telephone").text().replaceAll("Office Phone: ", ""));
				
				address2.setCity(mainOffice2.getElementsByAttributeValue("itemprop", "addressLocality").get(0).text().replace(",",""));
				address2.setPostalCode(mainOffice2.getElementsByAttributeValue("itemprop", "postalCode").text());
				address2.setState(USState.valueOf(mainOffice2.getElementsByAttributeValue("itemprop", "addressRegion").text()));
				//Wrong way, come back to later
				address2.setLine1(mainOffice2.getElementById("locStreetContent_additionalLocContent_0").text().replace(",","").split(" P")[0]);
				
				
				
			}
				
				


			
			
			
			
			//Office2
			
			office1.setLanguages(languages1);
			office1.setOfficeHours(office1Hours);
			office1.setAddress(address1);
			office2.setLanguages(languages2);
			office2.setOfficeHours(office2Hours);
			office2.setAddress(address2);		
			
			// Set offices
			List<Office> offices = new ArrayList<Office>();
			offices.add(office1);
			offices.add(office2);
			
			localAgent.setOffices(offices);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		return localAgent;
	}

	private USState USstate() {
		// TODO Auto-generated method stub
		return null;
	}
}
