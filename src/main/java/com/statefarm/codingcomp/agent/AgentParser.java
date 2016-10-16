package com.statefarm.codingcomp.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.parser.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import com.statefarm.codingcomp.bean.*;
import com.statefarm.codingcomp.utilities.SFFileReader;

@Component
public class AgentParser {
	@Autowired
	private SFFileReader sfFileReader;
	AgentParser() {
		
	}
	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
		Agent agent = new Agent();
		Office office = new Office();
		Address address = new Address();
		String lines = sfFileReader.readFile(fileName);
		Document doc = Jsoup.parse(lines);
		String agentName = doc.getElementById("AgentNameLabelId").select("span[itemprop]").text();
		agent.setName(agentName);
		String language = doc.select("div[title]").text();
		String[] languages = language.split(" ");
		TreeSet<String> languageSet = new TreeSet<String>();
		for(String s : languages) {
			languageSet.add(s.trim());
		}
		office.setLanguages(languageSet);
		Element ele = doc.getElementById("locationStreet_mainLocContent").parent();
		Elements elements = ele.select("span[id=locStreetContent_mainLocContent]");
		Document document = Jsoup.parse(elements.toString().replaceAll("<br>", "\n"));
		String line = document.select("span").text();
		address.setLine1(line);
		String city = ele.select("span[itemprop=addressLocality]").text();
		address.setCity(city);
		String state = ele.select("span[itemprop=addressRegion]").text();
		address.setState(USState.fromValue(state));
		String postalCode = ele.select("span[itemprop=postalCode]").text();
		address.setPostalCode(postalCode);
		office.setAddress(address);
		List<Office> off = new ArrayList<Office>();
		off.add(office);
		agent.setOffices(off);
		ele = doc.getElementById("defaultToggle");
		elements = ele.getElementsByAttributeValue("itemprop", "description");
		TreeSet<Product> products = new TreeSet<Product>();
		for(Element e : elements) {
			Elements listElements = e.select("li");
			for(Element e2 : listElements) {
				products.add(Product.fromValue(e2.text()));
			}
		}
		agent.setProducts(products);
		return agent;
	}
}
