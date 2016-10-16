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
		String line2 = streetAddrSplit.length > 1 ? streetAddrSplit[1] : null ;
		address.setLine1(line1);
		address.setLine2(line2);
		address.setPostalCode(addr.first().getElementsByAttributeValue("itemprop", "postalcode").first().html());
		String state = addr.first().getElementsByAttributeValue("itemprop", "addressRegion").first().html();
		address.setState(USState.valueOf(state));
		mainLoc.setAddress(address);
		Set<String> languages = null;
		mainLoc.setLanguages(languages);
		List<String> officeHours = null;
		mainLoc.setOfficeHours(officeHours);
		String phoneNumber = null;
		mainLoc.setPhoneNumber(phoneNumber);

		Office secondLoc = new Office();

		mainLoc.setAddress(address);
		mainLoc.setLanguages(languages);
		mainLoc.setOfficeHours(officeHours);
		mainLoc.setPhoneNumber(phoneNumber);

		agent.setName(aName);
		agent.setProducts(products);
		agent.setOffices(offices);
		return agent;
	}
}
