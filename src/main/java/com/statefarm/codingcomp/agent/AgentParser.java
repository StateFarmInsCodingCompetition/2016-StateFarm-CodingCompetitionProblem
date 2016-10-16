package com.statefarm.codingcomp.agent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
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
		String s = sfFileReader.readFile(fileName);
		s = s.replaceAll("(\n)*", "");
		s = s.replaceAll("(\t)*", "");
		String aName = s.split("(agentName)")[1].split("<b>", 2)[1].split("</b>", 2)[0];
		Agent agent = new Agent();
		/*
		 * Create agent
		 */
		Set<Product> products = new HashSet<Product>();
		if (s.contains("Auto Insurance"))
			products.add(Product.AUTO);
		if (s.contains("Annuities"))
			products.add(Product.ANNUITIES);
		if (s.contains("Life Insurance"))
			products.add(Product.LIFE);
		if (s.contains("Health Insurance"))
			products.add(Product.HEALTH);
		if (s.contains("Banking Products"))
			products.add(Product.BANK);
		if (s.contains("Mutual Funds"))
			products.add(Product.MUTUAL_FUNDS);
		if (s.contains("Home and Property Insurance"))
			products.add(Product.HOME_AND_PROPERTY);
		List<Office> offices = new ArrayList<Office>();
		org.jsoup.nodes.Document d = Jsoup.parse(s);
		Elements addr = d.getElementsByAttributeValue("itemprop", "address");
		Office mainLoc = new Office();
		/*
		 * Create mainloc address
		 */
		Address address = new Address();
		Elements locality = addr.first().getElementsByAttributeValue("itemprop", "addresslocality");
		address.setCity(locality.html().replace(",", ""));
		String streetAddr = addr.first().getElementById("locStreetContent_mainLocContent").html();
		String[] streetAddrSplit = streetAddr.split("<br>", 2);
		String line1 = streetAddrSplit[0];
		String line2 = streetAddrSplit.length > 1 ? streetAddrSplit[1] : null;
		address.setLine1(line1);
		address.setLine2(line2);
		address.setPostalCode(addr.first().getElementsByAttributeValue("itemprop", "postalcode").first().html());
		String state = addr.first().getElementsByAttributeValue("itemprop", "addressRegion").first().html();
		address.setState(USState.valueOf(state));
		mainLoc.setAddress(address);

		Set<String> languages = new HashSet<String>();
		Elements langs = d.getElementsByAttributeValueContaining("id", "language");
		for (Element e : langs) {
			if (e.id().contains("mainLoc") && e.tag().getName().equals("div"))
				languages.add(e.id().substring(8, e.id().indexOf('_')));
		}
		mainLoc.setLanguages(languages);

		Elements hours = d.getElementsByAttributeValue("itemprop", "openingHours");
		List<String> officeHours = new ArrayList<String>();
		for (Element e : hours) {
			if (e.id().contains("mainLoc"))
				officeHours.add(e.html());
		}
		mainLoc.setOfficeHours(officeHours);

		String phoneNumber = d.getElementById("offNumber_mainLocContent").getElementsByTag("span").last().html();
		mainLoc.setPhoneNumber(phoneNumber);

		offices.add(mainLoc);
		for(int i = 1; i < addr.size(); i ++) {
				Office secondLoc = new Office();
				
				Address address2 = new Address();
				Elements locality2 = addr.last().getElementsByAttributeValue("itemprop", "addresslocality");
				address2.setCity(locality2.html().replace(",", ""));
				String streetAddr2 = addr.last().getElementById("locStreetContent_additionalLocContent_" + (i - 1)).html();
				streetAddrSplit = streetAddr2.split("<br>", 2);
				line1 = streetAddrSplit[0];
				line2 = streetAddrSplit.length > 1 ? streetAddrSplit[1] : null;
				address2.setLine1(line1);
				address2.setLine2(line2);
				address2.setPostalCode(addr.last().getElementsByAttributeValue("itemprop", "postalcode").first().html());
				state = addr.last().getElementsByAttributeValue("itemprop", "addressRegion").first().html();
				address2.setState(USState.valueOf(state));
				secondLoc.setAddress(address);

				languages = new HashSet<String>();
				langs = d.getElementsByAttributeValueContaining("id", "language");
				for (Element e : langs) {
					if (e.id().contains("additionalLoc") && e.tag().getName().equals("div"))
						languages.add(e.id().substring(8, e.id().indexOf('_')));
				}
				secondLoc.setLanguages(languages);

				hours = d.getElementsByAttributeValue("itemprop", "openingHours");
				List<String> officeHours2 = new ArrayList<String>();
				for (Element e : hours) {
					if (e.id().contains("additionalLoc"))
						officeHours2.add(e.html());
				}
				secondLoc.setOfficeHours(officeHours2);

//				phoneNumber = d.getElementById("offNumber_additionalLocContent").getElementsByTag("span").last().html();
				secondLoc.setPhoneNumber(phoneNumber);

				offices.add(secondLoc);
			

		}
		
		agent.setName(aName);
		agent.setProducts(products);
		agent.setOffices(offices);
		return agent;
	}
}
