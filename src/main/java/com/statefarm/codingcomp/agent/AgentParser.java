package com.statefarm.codingcomp.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.statefarm.codingcomp.bean.Address;
import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.bean.Office;
import com.statefarm.codingcomp.bean.USState;
import com.statefarm.codingcomp.utilities.SFFileReader;

@Component
public class AgentParser {
	@Autowired
	private SFFileReader sfFileReader;

	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
//		System.out.println(fileName);
		String file = sfFileReader.readFile(fileName);
//		System.out.println(file);
		Agent agent = new Agent();
		
		String reName = "<div\\s+id=\"AgentName\"\\s+class=\"row-fluid\\s+agentName\">\\s+<b>([a-zA-Z]+)\\s+([a-zA-Z]+)</b>\\s+</div>";

	    Pattern pName = Pattern.compile(reName);
	    Matcher mName = pName.matcher(file);
//	    System.out.println("====================HEY!=====================");
	    if (mName.find())
	    {
	    	agent.setName(mName.group(1));
//	    	System.out.println("Yay!");
//	    	for (int i = 0; i <= 2; i++) {
//	    		System.out.println(i + ": " + m.group(i));
//	    	}
	    }
//	    this is giving me a headache
	    
	    List<Office> offices = new ArrayList<Office>();
	    Office office1 = new Office();
	    
//	    String reAddr1 = "<div\\s+class=\"row-fluid\\s+\"\\s+itemprop=\"streetAddress\">\\s+<span\\s+id=\"locStreetContent_mainLocContent\"\\s+class=\"sfx-text\\s+\">\\s+([0-9a-zA-Z,\\.\\s]+)\\s*(<br>([0-9a-zA-Z,\\.\\s]+[0-9a-zA-Z,\\.]))?\\s+</span>\\s+</div>\\s+<div\\s+class=\"row-fluid\">\\s+<span\\s+class=\"sfx-text\\s+\">\\s+<span\\s+itemprop=\"addressLocality\">([0-9a-zA-Z,\\.\\s]+),</span>\\s+<span\\s+itemprop=\"addressRegion\">([A-Z][A-Z])</span>\\s+<span\\s+itemprop=\"postalCode\">([0-9]{5}[-[0-9]{4}]?)</span>\\s+</span>\\s+</div>";
	    String reAddr1 = "<div\\s+class=\"row-fluid\\s+\"\\s+itemprop=\"streetAddress\">\\s+<span\\s+id=\"locStreetContent_mainLocContent\"\\s+class=\"sfx-text\\s+\">\\s+([0-9a-zA-Z,\\.\\s]+)\\s*(<br>([0-9a-zA-Z,\\.\\s]+[0-9a-zA-Z,\\.]))?\\s*</span>\\s+</div>\\s+<div\\s+class=\"row-fluid\">\\s+<span\\s+class=\"sfx-text\\s+\">\\s+<span\\s+itemprop=\"addressLocality\">([0-9a-zA-Z,\\.\\s]+),</span>\\s+<span\\s+itemprop=\"addressRegion\">([A-Z][A-Z])</span>\\s+<span\\s+itemprop=\"postalCode\">([0-9]{5}(-[0-9][0-9][0-9][0-9])?)";
	    Pattern pAddr1 = Pattern.compile(reAddr1);
	    Matcher mAddr1 = pAddr1.matcher(file);
//	    System.out.println("====================HEYO!=====================");
//	    if (agent.getName().equals("Kevin"))
	    if (mAddr1.find())
	    {
//	    	System.out.println("====================--> INTO LOOP!=====================");
	    	Address main = new Address();
	    	main.setLine1(mAddr1.group(1).trim());
	    	main.setLine2(mAddr1.group(3));
	    	main.setCity(mAddr1.group(4));
//	    	main.setState("NV");
//	    	main.setState(USState.fromValue("Nevada"));
//	    	System.out.println(main.getState().getValue());
//	    	main.setState(USState.fromValue(mAddr1.group(5)));
	    	main.setPostalCode(mAddr1.group(6));
//	    	System.out.println(main.getLine1());
//	    	System.out.println(main.getLine2());
//	    	System.out.println(main.getCity());
//	    	System.out.println(main.getState().getValue());
//	    	System.out.println(main.getPostalCode());
	    	
	    	switch (mAddr1.group(5)) {
	    		case "AL": main.setState(USState.AL); break;
	    		case "AK": main.setState(USState.AK); break;
	    		case "AZ": main.setState(USState.AZ); break;
	    		case "AR": main.setState(USState.AR); break;
	    		case "CA": main.setState(USState.CA); break;
	    		case "CO": main.setState(USState.CO); break;
	    		case "CT": main.setState(USState.CT); break;
	    		case "DE": main.setState(USState.DE); break;
	    		case "FL": main.setState(USState.FL); break;
	    		case "GA": main.setState(USState.GA); break;
	    		case "HI": main.setState(USState.HI); break;
	    		case "ID": main.setState(USState.ID); break;
	    		case "IL": main.setState(USState.IL); break;
	    		case "IN": main.setState(USState.IN); break;
	    		case "IA": main.setState(USState.IA); break;
	    		case "KS": main.setState(USState.KS); break;
	    		case "KY": main.setState(USState.KY); break;
	    		case "LA": main.setState(USState.LA); break;
	    		case "ME": main.setState(USState.ME); break;
	    		case "MD": main.setState(USState.MD); break;
	    		case "MA": main.setState(USState.MA); break;
	    		case "MI": main.setState(USState.MI); break;
	    		case "MN": main.setState(USState.MN); break;
	    		case "MS": main.setState(USState.MS); break;
	    		case "MO": main.setState(USState.MO); break;
	    		case "MT": main.setState(USState.MT); break;
	    		case "NE": main.setState(USState.NE); break;
	    		case "NV": main.setState(USState.NV); break;
	    		case "NJ": main.setState(USState.NJ); break;
	    		case "NM": main.setState(USState.NM); break;
	    		case "NY": main.setState(USState.NY); break;
	    		case "NC": main.setState(USState.NC); break;
	    		case "ND": main.setState(USState.ND); break;
	    		case "OH": main.setState(USState.OH); break;
	    		case "OK": main.setState(USState.OK); break;
	    		case "OR": main.setState(USState.OR); break;
	    		case "PA": main.setState(USState.PA); break;
	    		case "RI": main.setState(USState.RI); break;
	    		case "SC": main.setState(USState.SC); break;
	    		case "SD": main.setState(USState.SD); break;
	    		case "TN": main.setState(USState.TN); break;
	    		case "TX": main.setState(USState.TX); break;
	    		case "UT": main.setState(USState.UT); break;
	    		case "VT": main.setState(USState.VT); break;
	    		case "VA": main.setState(USState.VA); break;
	    		case "WA": main.setState(USState.WA); break;
	    		case "DC": main.setState(USState.DC); break;
	    		case "WV": main.setState(USState.WV); break;
	    		case "WI": main.setState(USState.WI); break;
	    		case "WY": main.setState(USState.WY); break;
//	    		eww
	    	}
	    	
//	    	System.out.println(main.getState().getValue());
	    	office1.setAddress(main);
	    }
	    
	    offices.add(office1);
	    
	    Office office2 = new Office();
	    
	    String reAddr2 = "<div\\s+class=\"row-fluid\\s+\"\\s+itemprop=\"streetAddress\">\\s+<span\\s+id=\"locStreetContent_additionalLocContent_0\"\\s+class=\"sfx-text\\s+\">\\s+([0-9a-zA-Z,\\.\\s]+)\\s*(<br>([0-9a-zA-Z,\\.\\s]+[0-9a-zA-Z,\\.]))?\\s+</span>\\s+</div>\\s+<div\\s+class=\"row-fluid\">\\s+<span\\s+class=\"sfx-text\\s+\">\\s+<span\\s+itemprop=\"addressLocality\">([0-9a-zA-Z,\\.\\s]+),</span>\\s+<span\\s+itemprop=\"addressRegion\">([A-Z][A-Z])</span>\\s+<span\\s+itemprop=\"postalCode\">([0-9]{5}[-[0-9]{4}]?)</span>\\s+</span>\\s+</div>";
	    Pattern pAddr2 = Pattern.compile(reAddr2);
	    Matcher mAddr2 = pAddr2.matcher(file);
//	    System.out.println("====================HEYO2!=====================");
//	    if (agent.getName().equals("Debbie"))
	    if (mAddr2.find())
	    {
//	    	System.out.println("====================--> INTO LOOP2!=====================");
	    	Address secondary = new Address();
	    	secondary.setLine1(mAddr2.group(1).replace(",", ""));
	    	secondary.setLine2(mAddr2.group(3));
	    	secondary.setCity(mAddr2.group(4));
	    	secondary.setPostalCode(mAddr2.group(6));
//	    	System.out.println(secondary.getLine1());
//	    	System.out.println(secondary.getLine2());
//	    	System.out.println(secondary.getCity());
//	    	System.out.println(secondary.getState().getValue());
//	    	System.out.println(secondary.getPostalCode());
//	    	System.out.println(mAddr2.group(5));
	    	
	    	switch (mAddr2.group(5)) {
	    		case "AL": secondary.setState(USState.AL); break;
	    		case "AK": secondary.setState(USState.AK); break;
	    		case "AZ": secondary.setState(USState.AZ); break;
	    		case "AR": secondary.setState(USState.AR); break;
	    		case "CA": secondary.setState(USState.CA); break;
	    		case "CO": secondary.setState(USState.CO); break;
	    		case "CT": secondary.setState(USState.CT); break;
	    		case "DE": secondary.setState(USState.DE); break;
	    		case "FL": secondary.setState(USState.FL); break;
	    		case "GA": secondary.setState(USState.GA); break;
	    		case "HI": secondary.setState(USState.HI); break;
	    		case "ID": secondary.setState(USState.ID); break;
	    		case "IL": secondary.setState(USState.IL); break;
	    		case "IN": secondary.setState(USState.IN); break;
	    		case "IA": secondary.setState(USState.IA); break;
	    		case "KS": secondary.setState(USState.KS); break;
	    		case "KY": secondary.setState(USState.KY); break;
	    		case "LA": secondary.setState(USState.LA); break;
	    		case "ME": secondary.setState(USState.ME); break;
	    		case "MD": secondary.setState(USState.MD); break;
	    		case "MA": secondary.setState(USState.MA); break;
	    		case "MI": secondary.setState(USState.MI); break;
	    		case "MN": secondary.setState(USState.MN); break;
	    		case "MS": secondary.setState(USState.MS); break;
	    		case "MO": secondary.setState(USState.MO); break;
	    		case "MT": secondary.setState(USState.MT); break;
	    		case "NE": secondary.setState(USState.NE); break;
	    		case "NV": secondary.setState(USState.NV); break;
	    		case "NJ": secondary.setState(USState.NJ); break;
	    		case "NM": secondary.setState(USState.NM); break;
	    		case "NY": secondary.setState(USState.NY); break;
	    		case "NC": secondary.setState(USState.NC); break;
	    		case "ND": secondary.setState(USState.ND); break;
	    		case "OH": secondary.setState(USState.OH); break;
	    		case "OK": secondary.setState(USState.OK); break;
	    		case "OR": secondary.setState(USState.OR); break;
	    		case "PA": secondary.setState(USState.PA); break;
	    		case "RI": secondary.setState(USState.RI); break;
	    		case "SC": secondary.setState(USState.SC); break;
	    		case "SD": secondary.setState(USState.SD); break;
	    		case "TN": secondary.setState(USState.TN); break;
	    		case "TX": secondary.setState(USState.TX); break;
	    		case "UT": secondary.setState(USState.UT); break;
	    		case "VT": secondary.setState(USState.VT); break;
	    		case "VA": secondary.setState(USState.VA); break;
	    		case "WA": secondary.setState(USState.WA); break;
	    		case "DC": secondary.setState(USState.DC); break;
	    		case "WV": secondary.setState(USState.WV); break;
	    		case "WI": secondary.setState(USState.WI); break;
	    		case "WY": secondary.setState(USState.WY); break;
	    	}
//	    	secondary.setState(USState.CO);
//	    	secondary.setState(USState.fromValue("Colorado"));
	    	
//	    	System.out.println(secondary.getState().getValue());
	    	office2.setAddress(secondary);
	    	offices.add(office2);
	    }
	    
	    agent.setOffices(offices);
		return agent;
	}
}