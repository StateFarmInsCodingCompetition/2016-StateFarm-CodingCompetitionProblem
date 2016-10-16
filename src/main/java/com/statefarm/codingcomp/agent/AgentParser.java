package com.statefarm.codingcomp.agent;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.statefarm.codingcomp.bean.*;
import com.statefarm.codingcomp.utilities.SFFileReader;

@Component
public class AgentParser {
	@Autowired
	private SFFileReader sfFileReader;
	//comment
	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
		String s = sfFileReader.readFile(fileName);
		Agent agent = new Agent();
		//System.out.print(s);
		//System.out.print(fileName);
		Document doc = Jsoup.parse(s);
		Elements elems = doc.getElementsByAttributeValue("itemprop", "description");
		Element productList = elems.get(0);
		Elements products = productList.getElementsByClass("li");
		

		Set<Product> prodSet = new HashSet<Product>();
		for (int i = 0; i < products.size(); i++) {
			prodSet.add(Product.fromValue(products.get(i).text()));
		}
		agent.setProducts(prodSet);
		
		/*
		String beginLicense = "Products Offered/Serviced by this Agent\"><div itemprop=\"description\">";
		String endLicense = "<div id=\"finraContent\"";
		int startIdx = s.indexOf(beginLicense);  //location of products licensed to sell
		int endIdx = s.indexOf(endLicense);
		String list = s.substring(startIdx + beginLicense.length(), endIdx);
		//Product[] licenses = ; 
		String list = fStS(s, beginLicense, endLicense);
		//Set<Product> products = new HashSet<Product>(Arrays.asList(splitUL(list)));
		Agent agent = new Agent();
		agent.setProducts(splitUL(list));
		return agent;
		*/
		return agent;
	}
	
	public String fStS(String s, String begin, String end) {
		int startIdx = s.indexOf(begin);
		int endIdx = s.indexOf(end);
		String sub = s.substring(startIdx + begin.length(), endIdx);
		return sub;
	}
	
	public Set<Product> splitUL(String ul) {
		ul = ul.trim();
		ul = ul.replace("<ul>", "").trim();
		ul = ul.replace("</ul>", "").trim();
		String[] li = ul.split("<li>");
		//Product[] li2 = new Product[li.length];
		Set<Product> li2 = new HashSet<Product>();
		for (int i = 1; i < li.length; i++) {
			String str = li[i].replace("</li>", "").trim();
			Product p = Product.fromValue(str);
			System.out.print(p.getValue());
			li2.add(p);
		}
		return li2;
	}
}
