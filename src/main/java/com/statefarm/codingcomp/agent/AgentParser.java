package com.statefarm.codingcomp.agent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		sfFileReader = new SFFileReader();
		Agent agent = new Agent();
		String info = sfFileReader.readFile(fileName);
		
		//Products
		Set<Product> products = new HashSet<Product>();
		int index = info.indexOf("Products Offered/Serviced by this Agent");
		index = info.indexOf("<ul>", index);
		int index2 = info.indexOf("</ul>", index);
		String s = info.substring(index, index2);
		int location = 8;
		while(location < s.length()) {
			int x = s.indexOf("<", location) - 1;
			String s2 = s.substring(location, x);
			Product prod = Product.fromValue(s2);
			if(prod != null) {
				products.add(prod);
			}
			else {
				System.err.println("Enum value not found.  Did you mess something up with your parsing?");
			}
			location = x + 10;
		}
		agent.setProducts(products);
		
		
		//Name
		int namePosition = info.indexOf("agentName\">") + 21	;
		int nameEnd = info.indexOf("</b>", namePosition);
		String name = info.substring(namePosition, nameEnd);
		agent.setName(name);
		
		
		//Offices
		List<Office> officelist = new ArrayList<Office>();
		int offnum = 1;
		int firoff = info.indexOf("Main Location");
		int secoff;
		if((secoff = info.indexOf("Secondary Office")) > -1) {
			offnum++;
		}
		for(int i = 0; i < offnum; i++) {
			Office office = new Office();
			int loc;
			if(i == 0) {
					loc = firoff;
			}
			else {
				loc = secoff;
			}
			
			
			// Languages
			Set<String> langs = new HashSet<String>();
			int langloc = info.indexOf("Languages", loc);
			langloc = info.indexOf("<ul>", langloc);
			int langloc2 = info.indexOf("</ul>", langloc);
			String langsub = info.substring(langloc, langloc2);
			int x = 0;
			//System.out.println(langsub);
			while((x = langsub.indexOf("sfx-text", x)) > -1) {
				x += 10;
				int x1 = langsub.indexOf("\"", x) + 1;
				int x2 = langsub.indexOf("\"", x1);
				String langsubsub = langsub.substring(x1, x2);
				//System.out.println(langsubsub);
				langs.add(langsubsub);
			}
			office.setLanguages(langs);
			
			
			// Phone Number
			int phonePosition = info.indexOf(";</b><span>", loc) + 11;
			int phoneEnd = info.indexOf("</span>", phonePosition);
			String phone = info.substring(phonePosition, phoneEnd);
			office.setPhoneNumber(phone);
			
			
			// Office Hours
			ArrayList<String> officehours = new ArrayList<String>();
			int hoursloc = info.indexOf("Office Hours", loc);
			int hourslocend = info.indexOf("tenPixel-topSpace", hoursloc);
			String hourssub = info.substring(hoursloc, hourslocend);
			x = 0;
			while((x = hourssub.indexOf("sfx-text", x)) > -1) {
				x += 11;
				int x2 = hourssub.indexOf("</span>", x);
				String hourssubsub = hourssub.substring(x, x2);
				//System.out.println(hourssubsub);
				officehours.add(hourssubsub);
			}
			officehours.trimToSize();
			office.setOfficeHours(officehours);
			
			
			// Address
			//Parse file to find Street
			Address add = new Address();
			int addressPosition = info.indexOf("Content_mainLocContent", loc) + 47;
			int br = info.indexOf("<br>", loc);
			int addressEnd = info.indexOf("\n", addressPosition);
			String address1;
			String address2;
			if (br > addressEnd) {
				address1 = info.substring(addressPosition, addressEnd);
				add.setLine1(address1);
			}
			else if (addressEnd > br) {
				address1 = info.substring(addressPosition, br);
				add.setLine1(address1);
				int span = info.indexOf("</span>", loc) - 5;
				address2 = info.substring(br + 4, span);
				add.setLine2(address2);
			}
			
			//Parse file to find City
			int cityPosition = info.indexOf("addressLocality", loc) + 17;
			int cityEnd = info.indexOf(",", cityPosition);
			String city = info.substring(cityPosition, cityEnd);
			add.setCity(city);
			
			//Parse file to find State
			int statePosition = info.indexOf("addressRegion", loc) + 15;
			int stateEnd = info.indexOf("<", statePosition);
			String state = info.substring(statePosition, stateEnd);
			add.setState(USState.valueOf(state));
			
			//Parse file to find Postal Code
			int postalPosition = info.indexOf("postalCode", loc) + 12;
			int postalEnd = info.indexOf("<", postalPosition);
			String postal = info.substring(postalPosition, postalEnd);
			add.setPostalCode(postal);
			
			office.setAddress(add);
			
			officelist.add(office);
		}
		agent.setOffices(officelist);
		
		return agent;
	}
}
