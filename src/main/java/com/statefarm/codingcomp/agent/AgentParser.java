package com.statefarm.codingcomp.agent;

import java.io.File;
import java.io.IOException;
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
import com.statefarm.codingcomp.utilities.SFFileReader;

@Component
public class AgentParser {
	@Autowired
	private SFFileReader sfFileReader;

	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) throws IOException {
		
		Agent agent = new Agent();
		File in = new File (fileName);
		Document document = Jsoup.parse(in, null);
		Elements products = document.select("#sfx_defaultToggle_div li");	
		
		/*** Products ***/
		Set<Product> agentProducts  = new HashSet<Product>(); 
		
		for (Element answers: products) {
			String strProduct = answers.toString();
			strProduct = strProduct.substring(strProduct.indexOf(">") + 1, strProduct.lastIndexOf("<") - 1);
			
			agentProducts.add(Product.fromValue(strProduct));
		}
		agent.setProducts(agentProducts);
		
		/*** Name ***/
		Elements agentName = document.select("#AgentNameLabelId span[itemprop=\"name\"]");
		String strAgentName = agentName.toString(); 
		strAgentName = strAgentName.substring(strAgentName.indexOf(">") + 1, strAgentName.lastIndexOf("<"));
		agent.setName(strAgentName);
		
		/*** Main Office ***/
		/*** Languages ***/
		Office mainOffice = new Office();
		Set<String> officeLangs = new HashSet<String>();
		
		Elements primOfficeMainLang = document.select("div[id^=\"language\"][id$=\"mainLocContent\"] div");
		String strMainPrimLang = primOfficeMainLang.toString(); 
		strMainPrimLang = strMainPrimLang.substring(strMainPrimLang.indexOf("=\"") + 2, strMainPrimLang.lastIndexOf("\">"));

		Elements primOfficeSecLang = document.select("div[id^=\"language\"][id$=\"mainLocContent_0\"] div");
		String strMainSecLang = primOfficeSecLang.toString(); 
		strMainSecLang = strMainSecLang.substring(strMainSecLang.indexOf("=\"") + 2, strMainSecLang.lastIndexOf("\">"));
		
		officeLangs.add(strMainPrimLang);
		officeLangs.add(strMainSecLang);
		
		mainOffice.setLanguages(officeLangs);
		
		/*** Address ***/
		Address mainAddress = new Address();
		
		/*** City ***/
		Elements mainAddressCity = document.select("div[itemprop=\"address\"] span[itemprop=\"addressLocality\"]");
		String strCity = mainAddressCity.toString(); 
		strCity = strCity.substring(strCity.indexOf(">") + 1, strCity.lastIndexOf(",<"));
		
		System.out.println("City: "+strCity);
		
		
		/*** Secondary Office ***/
		/*** Languages ***/
		Set<String> secOfficeLangs = new HashSet<String>();		
		Office secOffice = new Office();

		Elements secOfficeMainLang = document.select("div[id^=\"language\"][id$=\"mainLocContent\"] div");
		String strSecPrimLang = secOfficeMainLang.toString(); 
		strSecPrimLang = strSecPrimLang.substring(strSecPrimLang.indexOf("=\"") + 2, strSecPrimLang.lastIndexOf("\">"));

		Elements secOfficeSecLang = document.select("div[id^=\"language\"][id$=\"mainLocContent_0\"] div");
		String strSecSecLang = secOfficeSecLang.toString(); 
		strSecSecLang = strSecSecLang.substring(strSecSecLang.indexOf("=\"") + 2, strSecSecLang.lastIndexOf("\">"));
		
		secOfficeLangs.add(strSecPrimLang);
		secOfficeLangs.add(strSecSecLang);
		
		secOffice.setLanguages(secOfficeLangs);
		
		/*** Set Offices ***/
		List<Office> agentOffices = new ArrayList<Office>();
		agentOffices.add(mainOffice);
		agentOffices.add(secOffice);
		
		agent.setOffices(agentOffices);

		return agent;
	}
}
