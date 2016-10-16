package com.statefarm.codingcomp.agent;

import com.statefarm.codingcomp.bean.*;
import com.statefarm.codingcomp.utilities.SFFileReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
/**
 * AgentParser parses a HTML document into an Agent object.
 */
public class AgentParser {
	@Autowired
	private SFFileReader sfFileReader;

	@Cacheable(value = "agents")
    /**
     * Parses a HTML document to extract an Agent object
     *
     * @param fileName String the name of the HTML file
     * @returns Agent the parsed agent data
     */
	public Agent parseAgent(String fileName) {
		Document doc = Jsoup.parse(sfFileReader.readFile(fileName));

        List<Office> officeList = new ArrayList<>();
        for(Element officeElement : doc.getElementById("tabGroupOffice").getElementsByClass("tab-pane")){
            Office office = new Office();

            USState state = null;
            String stateCode = officeElement.getElementsByAttributeValue("itemprop", "addressRegion").text();

            for(USState usState : USState.values()){
                if(usState.name().toLowerCase().equals(stateCode.toLowerCase())){
                    state = usState;
                    break;
                }
            }

            Address address = new Address();
            String[] splitAddress = officeElement.getElementsByClass("span5").get(0).getElementsByAttributeValue("itemprop", "streetAddress").get(0).getElementsByTag("span").html().trim().split("<br>");
            address.setLine1(splitAddress[0].replaceAll(",", ""));

            if(splitAddress.length > 1){
                address.setLine2(splitAddress[1]);
            }

            address.setCity(officeElement.getElementsByAttributeValue("itemprop", "addressLocality").text().replaceAll(",", ""));
            address.setPostalCode(officeElement.getElementsByAttributeValue("itemprop", "postalCode").text());
            address.setState(state);

            office.setAddress(address);
            office.setOfficeHours(officeElement.getElementsByAttributeValue("itemprop", "openingHours").stream().map(Element::text).collect(Collectors.toList()));
            office.setPhoneNumber(officeElement.getElementsByAttributeValueStarting("id", "offNumber").get(0).getElementsByTag("span").get(0).text().substring(14));
            office.setLanguages(officeElement.getElementsByClass("span5").get(1).getElementsByTag("li").stream().map(e -> e.getElementsByAttribute("title").attr("title")).collect(Collectors.toSet()));

            officeList.add(office);
        }


		Agent agent = new Agent();
        agent.setName(doc.getElementById("AgentNameLabelId").getElementsByAttributeValue("itemprop", "name").text());
		agent.setProducts(doc.getElementsByAttributeValue("aria-label", "Products Offered/Serviced by this Agent").get(0).getElementsByTag("li").stream().map(e -> Product.fromValue(e.text())).collect(Collectors.toSet()));
		agent.setOffices(officeList);

		return agent;
	}
}
