package com.statefarm.codingcomp.agent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
		try
		{
			Set<Product> products = new HashSet<>();
			Agent agent = new Agent();
			Office primaryOffice = new Office();
			Office secondaryOffice = new Office();
			
			File agentFile = new File(fileName);
		//	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		//	DocumentBuilder agentBuilder = dbf.newDocumentBuilder();
			Document agentDoc = Jsoup.parse(agentFile, "UTF-8");
			Element productElement = agentDoc.select("div[itemprop='description']").first();
			//System.out.println(productElement.toString());

			Elements productsElements = productElement.select("li");
		//	System.out.println(productsElements.toString());

			for(Element e: productsElements)
			{
				products.add(Product.fromValue(e.html()));			

			}
			Element nameElement = agentDoc.select("span[itemprop='name']").first();
		
			
			Element primaryOfficeElement = agentDoc.select("span[id='offNumber_mainLocContent']").first();
			Element phoneElement = primaryOfficeElement.select("span").get(1);
			primaryOffice.setPhoneNumber(phoneElement.html());

			Element secondaryOfficeElement = agentDoc.select("span[id='offNumber_additionalLocContent_0']").first();
			if(secondaryOfficeElement != null)
			{
				Element secondaryPhoneElement = secondaryOfficeElement.select("span").get(1);
				secondaryOffice.setPhoneNumber(secondaryPhoneElement.html());
				System.out.println(secondaryPhoneElement.html());
			}
			
			List<String> primaryOfficeHours = new ArrayList<>();
			List<String> secondaryOfficeHours = new ArrayList<>();
			
			//Find the primary office hours and add them.
			Elements primaryOfficeHoursElements = agentDoc.select("span[itemprop='openingHours'][id^=officeHoursContent_mainLocContent]");
			for(Element e: primaryOfficeHoursElements)
			{
				primaryOfficeHours.add(e.html());
				System.out.println(e.html());

			}
			primaryOffice.setOfficeHours(primaryOfficeHours);
			
			//Find the secondary office hours and add them.
			Elements secondaryOfficeHoursElements = agentDoc.select("span[itemprop='openingHours'][id^=officeHoursContent_additionalLocContent]");
			for(Element e: secondaryOfficeHoursElements)
			{
				secondaryOfficeHours.add(e.html());
				System.out.println(e.html());

			}
			
			primaryOffice.setOfficeHours(primaryOfficeHours);
			secondaryOffice.setOfficeHours(secondaryOfficeHours);
			
			Elements addressElements = agentDoc.select("div[itemprop='address']");
			for(Element address: addressElements)
			{
			//	Element primaryAddress = address.select("");
				Element primaryAddress = address.select("span[id='locStreetContent_mainLocContent'").first();
				if(primaryAddress != null)
				{
					String addressString = primaryAddress.html().replaceAll(",", "");
					String addressStrings[] = addressString.split("<br>");
					
					Address primaryAddressLocation = new Address();
					primaryAddressLocation.setLine1(addressStrings[0]);
					if(addressStrings.length > 1)
					{
						primaryAddressLocation.setLine2(addressStrings[1]);
					}
					
					Element postalCodeElement  = address.select("span[itemprop='postalCode']").first();
					primaryAddressLocation.setPostalCode(postalCodeElement.html());
					
					Element regionElement  = address.select("span[itemprop='addressRegion']").first();
					primaryAddressLocation.setState(USState.valueOf(regionElement.html()));
					
					Element cityElement = address.select("span[itemprop='addressLocality']").first();
					primaryAddressLocation.setCity(cityElement.html().replaceAll(",", ""));
					
					primaryOffice.setAddress(primaryAddressLocation);
				}
				
				Element secondaryAddress = address.select("span[id='locStreetContent_additionalLocContent_0'").first();
				if(secondaryAddress != null)
				{
					String addressString = secondaryAddress.html().replaceAll(",", "");
					String addressStrings[] = addressString.split("<br>");
					Address secondaryAddressLocation = new Address();
					secondaryAddressLocation.setLine1(addressStrings[0]);
					secondaryAddressLocation.setLine2(addressStrings[1]);
					
					
					Element postalCodeElement  = address.select("span[itemprop='postalCode']").first();
					secondaryAddressLocation.setPostalCode(postalCodeElement.html());
					
					Element regionElement  = address.select("span[itemprop='addressRegion']").first();
					secondaryAddressLocation.setState(USState.valueOf(regionElement.html()));
					
					Element cityElement = address.select("span[itemprop='addressLocality']").first();
					secondaryAddressLocation.setCity(cityElement.html().replaceAll(",", ""));
					
					secondaryOffice.setAddress(secondaryAddressLocation);

//<span class="sfx-text ">  locStreetContent_mainLocContent
				}
				

				//languageEnglish_mainLocContent
				//languageEnglish_additionalLocContent
			}
			
			//
	//		Element secondaryLanguageElement = agentDoc.select("div[id='languageEnglish_additionalLocContent_0'");
	//		System.out.print(primaryLanguageElements);
		
		//	itemprop="address"
			
			
			List<Office> offices = new ArrayList<>();
			offices.add(primaryOffice);
			offices.add(secondaryOffice);
			
			agent.setOffices(offices);
			//additionalLocContent_0
			agent.setName(nameElement.html());
			agent.setProducts(products);
			return agent;
		}
		
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.getStackTrace();
		}
		
		return null;
	}
}
