package com.statefarm.codingcomp.agent;

import com.statefarm.codingcomp.bean.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.stereotype.Component;

import com.statefarm.codingcomp.utilities.SFFileReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AgentParser {
	@Autowired
	private SFFileReader sfFileReader;

	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
		String s = sfFileReader.readFile(fileName);
		Document html = Jsoup.parse(s);
		Agent agent = new Agent();
		agent.setName(getName(html));
		ArrayList<Office> offices = new ArrayList<>();
		for(Element office : html.getElementById("tabGroupOffice").children()) {
			if(office.children().size() == 0 || office.tagName().equals("h2")){
				continue;
			}
			Office o = new Office();
			o.setAddress(getAddress(office));
			o.setLanguages(getLangs(office));
			o.setOfficeHours(getOfficeHours(office));
			o.setPhoneNumber(getPhoneNumber(office));
			offices.add(o);
		}
		agent.setOffices(offices);
		agent.setProducts(getProducts(html));
		return agent;
	}

	private String getName(Element html) {
		return html.getElementById("AgentNameLabelId").child(0).text();
	}
	private Address getAddress(Element root) {
		Element address = null;
		for(Element e : root.child(0).children()) {
			if(e.attributes().toString().contains("itemprop=\"address\"")) {
				address = e;
				break;
			}
		}
		if(address == null) {
			for(Element e : root.child(0).child(0).children()) {
				if(e.attributes().toString().contains("itemprop=\"address\"")) {
					address = e;
					break;
				}
			}
		}
		String line1 = address.child(0).child(0).toString().split(">")[1].split("<")[0].trim();
		String l2 = null;
		if(address.child(0).child(0).toString().contains("br>"))
			l2 = address.child(0).child(0).toString().split("br>")[1].split("<")[0].trim();

		Element spanner = address.child(1).child(0);
		String line2 = "";
		for(Node e : spanner.childNodes()) {
			if(e.toString().trim().equals("")) continue;
			line2 += e.toString().split(">")[1].split("<")[0] + " ";
		}
		Address add = new Address();
		add.setCity(line2.split(",")[0]);
		add.setLine1(line1.replace(",", ""));
		add.setLine2(l2);
		add.setPostalCode(line2.split(",")[1].trim().split(" ")[1]);
		add.setState(USState.valueOf(line2.split(",")[1].trim().split(" ")[0]));
		return add;
	}
	private Set<String> getLangs(Element root) {
		Set<String> langs = new HashSet<>();
		for(Element language : root.getElementsByTag("li")) {
			Element child = language.children().first();
			while(child.children().size() != 0) {
				child = child.children().first();
			}
			langs.add(child.attr("title"));
		}
		return langs;
	}
	private List<String> getOfficeHours(Element root) {
		List<String> officeHours = new ArrayList<>();
		for(Element child : root.getElementsByAttributeValue("itemprop", "openingHours")) {
			officeHours.add(child.text());
		}
		return officeHours;
	}
	private String getPhoneNumber(Element root) {
		return root.getElementsByAttributeValue("itemprop", "telephone").get(0).child(0).child(1).text();
	}
	private Set<Product> getProducts(Element html) {
		Set<Product> products = new HashSet<>();
		Elements productElements = html.getElementById("productsOfferedContent").getElementsByTag("ul").get(0).children();
		for (Element productElement: productElements) {
			products.add(Product.fromValue(productElement.text()));
		}
		return products;
	}
}
