package com.statefarm.codingcomp.agent;

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
	public Agent parseAgent(String fileName) {
		
		// I had some trouble understanding how to use sfFileReader, but after
		// looking at the source code it made more sense.
		String agent = sfFileReader.readFile(fileName);
		
		// I've never used Jsoup before so I'm not familiar with the API but I'm
		// quite impress, I'm going to start using it from now on.
		Document html = Jsoup.parse(agent);
		Element agentName = html.body().getElementById("AgentNameLabelId");
		Elements name = agentName.getElementsByAttribute("itemprop");
		String gotit = name.text();
		
		
		// the agent that is extracted from the html site
		Agent actualAgent = new Agent();
		
		// now that I've parse the name I need to set the actual agent's name
		actualAgent.setName(gotit);
		
		// now I need to parse the products
		Element productslist = html.body().getElementById("sfx_defaultToggle_div");
		Elements agentproducts = productslist.getElementsByAttributeValue("itemprop", "description");
		Elements products = productslist.getElementsByTag("li");
		
		Set<Product> setproducts = new HashSet<Product>();

		
		for(Element i : products){
			setproducts.add(Product.fromValue(i.text()));
		}
		
		// after parsing the list of products set them to the actual agent.
		actualAgent.setProducts(setproducts);
		
		// variables for the rest or the required parse information
		Office office1 = new Office();
		Office office2 = new Office();
		Set<String> languages1 = new HashSet<String>();
		Set<String> languages2 = new HashSet<String>();
		List<String> office1Hours = new ArrayList<String>();
		List<String> office2Hours = new ArrayList<String>();
		Address address1 = new Address();
		Address address2 = new Address();
		
		Element parseELang = html.body().getElementById("languageEnglish_mainLocContent");
		System.out.println(parseELang.text());
		languages1.add(parseELang.text());
		
		Element parseSLang = html.body().getElementById("languageSpanish_mainLocContent_0");
		System.out.println(parseSLang.text());
		languages2.add(parseSLang.text());
		
		Element parseOfficeHours = html.body().getElementById("officeHoursContent_mainLocContent_0");
		Element parseAddress1 = html.body().getElementById("mailAddressContent_mainLocContent");
		Element parseAddress2 = html.body().getElementById("mailAddressAdContent_mainLocContent");
		
		System.out.println(parseOfficeHours.text());
		System.out.println(parseAddress1.text());
		address1.setLine1(parseAddress1.text());
		
		// I lost a lot of time trying to understand how to use Jsoup and parse the html file.
		
		return null;
	}
}
