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
import com.statefarm.codingcomp.bean.USState;
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
		
		Element parseSLang = html.body().getElementById("languageSpanish_mainLocContent_0");
		
		Element parseOffice1Hours1 = html.body().getElementById("officeHoursContent_mainLocContent_0");
		Element parseOffice1Hours2 = html.body().getElementById("officeHoursContent_mainLocContent_1");
		Element parseOffice1Hours3 = html.body().getElementById("officeHoursContent_mainLocContent_2");

		Element parseAddress1Line1 = html.body().getElementById("mailAddressContent_mainLocContent");
		Element parseAddress1Line2 = html.body().getElementById("mailAddressAdContent_mainLocContent");
		Elements spans = parseAddress1Line2.getElementsByTag("span");
		
		Elements officephone = html.body().getElementById("offNumber_mainLocContent").getElementsByTag("span");
		String phone = officephone.get(1).text();
		
		List<String> address = new ArrayList<String>(); 
		for(Element m : spans){
			address.add(m.text());
		}
		
		languages1.add(parseELang.text());
		languages1.add(parseSLang.text());
		office1Hours.add(parseOffice1Hours1.text());
		office1Hours.add(parseOffice1Hours1.text());
		office1Hours.add(parseOffice1Hours1.text());
		address1.setLine1(parseAddress1Line1.text());
		address1.setLine2(null);
		address1.setCity(address.get(1));
		address1.setState(USState.fromValue(address.get(2)));
		address1.setPostalCode(address.get(3));
		office1.setLanguages(languages1);
		office1.setPhoneNumber(phone);
		office1.setOfficeHours(office1Hours);
		office1.setAddress(address1);
		

		Element secondOfficeELang = html.body().getElementById("languageEnglish_additionalLocContent_0");
		Element secondOfficeSLang = html.body().getElementById("languageSpanish_additionalLocContent_0_0");
		
		languages2.add(secondOfficeELang.text());
		languages2.add(secondOfficeSLang.text());
		
		Element secondOfficeHoursLine1 = html.body().getElementById("officeHoursContent_additionalLocContent_0_0");
		Element secondOfficeHoursLine2 = html.body().getElementById("officeHoursContent_additionalLocContent_0_1");
		Element secondOfficeHoursLine3 = html.body().getElementById("officeHoursContent_additionalLocContent_0_2");
		
		office2Hours.add(secondOfficeHoursLine1.text());
		office2Hours.add(secondOfficeHoursLine2.text());
		office2Hours.add(secondOfficeHoursLine3.text());
		
		Element secondoffice = html.body().getElementById("paneladditionalLoc_0");
		Elements firstline = secondoffice.select("span#locStreetContent_additionalLocContent_0");
		Elements secondlocal = secondoffice.getElementsByAttributeValue("itemprop", "addressLocality");
		Elements region = secondoffice.getElementsByAttributeValue("itemprop", "addressRegion");
		Elements postalcode = secondoffice.getElementsByAttributeValue("itemprop", "postalCode");
		
		String addressfirstline = firstline.text().substring(0,25);
		String addresssecondline = firstline.text().substring(25);
		
		address2.setLine1(addressfirstline);
		address2.setLine2(addresssecondline);
		address2.setCity(secondlocal.text());
		address2.setState(USState.fromValue(region.text()));
		address2.setPostalCode(postalcode.text());
		
		office2.setLanguages(languages2);
		office2.setPhoneNumber(phone);
		office2.setOfficeHours(office2Hours);
		office2.setAddress(address2);		
		
		List<Office> offices = new ArrayList<Office>();
		offices.add(office1);
		offices.add(office2);
		
		actualAgent.setOffices(offices);
		
		return actualAgent;
	}
}
