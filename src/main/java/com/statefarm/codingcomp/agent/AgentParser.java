package com.statefarm.codingcomp.agent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
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
	
//	public static void main(String[] args) {
//		String kevinParksWebsitePath = Paths.get("src", "test", "resources", "KevinParks.html").toString();
//		String debbiePeckWebsitePath = Paths.get("src", "test", "resources", "DebbiePeck.html").toString();
//		parseAgent(kevinParksWebsitePath);
//	}

	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
		//sfFileReader = new SFFileReader();
		Agent agent = new Agent();
		List<Office> officeList = new ArrayList<Office>();
		File input = new File(fileName);
		try {
			Document doc = Jsoup.parse(input, "UTF-8", "");
			Element locationElement = doc.getElementById("tabGroupOffice");
			officeList.add(getMainLocationInfo(doc));
			if (locationElement.getElementsByClass("tab-pane").size() > 1)
				officeList.add(getAdditionalOfficeInfo(doc));
			agent.setOffices(officeList);
			agent.setName(getAgentName(doc));
			agent.setProducts(getProductList(doc));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return agent;
	}
	
	public Office getMainLocationInfo(Document doc) {
		Office mainOffice = new Office();
		Element panelElement = doc.getElementById("panelmainLocation");
		mainOffice.setAddress(getMainAddress(panelElement, false));
		mainOffice.setLanguages(getMainLanguages(panelElement));
		mainOffice.setOfficeHours(getMainHours(panelElement));
		mainOffice.setPhoneNumber(getMainPhoneNumber(doc));
		return mainOffice;
	}
	
	public Office getAdditionalOfficeInfo(Document doc) {
		Office additionalLocation = new Office();
		Element panelElement = doc.getElementById("paneladditionalLoc_0");
		additionalLocation.setAddress(getMainAddress(panelElement, true));
		additionalLocation.setLanguages(getMainLanguages(panelElement));
		additionalLocation.setOfficeHours(getMainHours(panelElement));
		additionalLocation.setPhoneNumber(getAdditionalPhoneNumber(doc));
		return additionalLocation;
	}
	
	public Address getMainAddress(Element panelElement, boolean additionalLoc) {
		Element addressElement = panelElement.getElementsByAttributeValue("itemprop", "address").get(0);
		Element streetElement;
		if (additionalLoc)
			streetElement= panelElement.getElementById("locStreetContent_additionalLocContent_0");
		else
			streetElement= panelElement.getElementById("locStreetContent_mainLocContent");
		Element cityElement = addressElement.getElementsByAttributeValue("itemprop", "addressLocality").get(0);
		Element stateElement = addressElement.getElementsByAttributeValue("itemprop", "addressRegion").get(0);
		Element zipCodeElement = addressElement.getElementsByAttributeValue("itemprop", "postalCode").get(0);
		
		String[] tokens = streetElement.html().replaceAll("<br>", "\n").split("\n");
		String line1 = tokens[0].replaceAll("[^A-Za-z0-9. ]", "");
		String line2 = null;
		if (tokens.length > 1)
			line2 = tokens[1].replaceAll("[^A-Za-z0-9. ]", "");;
		String city = cityElement.text().replaceAll("[^A-Za-z ]", "");
		USState state = USState.valueOf(stateElement.text().replaceAll("[^A-Za-z]", ""));
		String zipcode = zipCodeElement.text().replaceAll("[^0-9-]", "");
		
		Address address = new Address();
		address.setLine1(line1);
		address.setLine2(line2);
		address.setCity(city);
		address.setState(state);
		address.setPostalCode(zipcode);
		return address;
	}
	
	public List<String> getMainHours(Element panelElement) {
		List<String> hoursList = new ArrayList<String>();
		//Element mainOfficeElement = doc.getElementById("panelmainLocation");
		Elements hourElements = panelElement.getElementsByAttributeValue("itemprop", "openingHours");
		for (Element element: hourElements)
			hoursList.add(element.text());
		return hoursList;
	}
	
	public Set<String> getMainLanguages(Element panelElement) {
		Set<String> languageSet = new HashSet<String>();
		//Element locationElement = doc.getElementById("panelmainLocation");
		Element subPanelElement = panelElement.getElementsByClass("span5").get(1);
		Elements languageElements = subPanelElement.getElementsByTag("li");
		for (Element element: languageElements) {
			String text = element.text();
			if (text.equals("Español"))
				languageSet.add("Spanish");
			else
				languageSet.add(element.text());
		}
			
		return languageSet;
	}
	
	public String getMainPhoneNumber(Document doc) {
		String phoneString = doc.getElementById("offNumber_mainLocContent").text();
		return phoneString.substring(phoneString.indexOf(":") + 2);
	}
	
	public String getAdditionalPhoneNumber(Document doc) {
		String additionalPhoneString = doc.getElementById("offNumber_additionalLocContent_0").text();
		return additionalPhoneString.substring(additionalPhoneString.indexOf(":") + 2);
	}
	
	public Set<Product> getProductList(Document other){		
		Elements productsoffered = other.getElementById("sfx_defaultToggle_div").getElementsByTag("li");
		Set<Product> productSet = new HashSet<Product>();
		for(Element token: productsoffered){
			String productString = token.text();
			Product product = Product.fromValue(productString);
			productSet.add(product);		
		}
		return productSet;
	}
	
	public String getAgentName(Document other){
		Element content = other.getElementById("AgentNameLabelId");
		String name = content.text();
		return name;
	}
	
	
}
