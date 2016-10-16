package com.statefarm.codingcomp.agent;

import java.util.ArrayList;

import java.util.HashSet;
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
	private int nextOffice = 0;
	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
		String fileString = sfFileReader.readFile(fileName);
		Agent myAgent = new Agent();
		String name = getName(fileString);
		myAgent.setName(name);
		Set<Product> products = product(fileString);
		myAgent.setProducts(products);
		ArrayList<Office> of = getOffices(fileString);
		myAgent.setOffices(of);
		
		
		
		return myAgent;
	}
	private String getName(String file){
		int index = file.indexOf("State Farm Agent")+18;
		String returnString="";
		int numspace=0;
		while (numspace<2){
			if (file.charAt(index)==' ') numspace++;
			if(numspace!=2){
			returnString+=file.charAt(index);
			}
			index++;
		}
		return returnString;
	}
	private Set<Product> product(String file){
		int index = file.indexOf("itemprop=description");
		String returnString= file.substring(index);
		int indexDiv = returnString.indexOf("</div");
		String myString = returnString.substring(0, indexDiv);
		String[] products = myString.split("</li><li>");
		
		Set<Product> mySet = new HashSet<Product>();
		for(int i = 1; i<products.length-1; i++){
			System.out.println(products[i]);
			mySet.add(Product.fromValue(products[i].trim()));
			
		}

		String firstProduct = products[0];
		int nextInd = firstProduct.indexOf("<li>");
		String fstP = firstProduct.substring(nextInd+4).trim();
		mySet.add(Product.fromValue(fstP));
		String lastProduct = products[products.length-1];
		int myInd = lastProduct.indexOf("</li>");
		String lastP = lastProduct.substring(0, myInd).trim();
		System.out.println(lastP);
		mySet.add(Product.fromValue(lastP));

		return mySet;
	}
	private Set<String> getLang(String file){
		int currInd = file.indexOf("<b>Languages</b>");
		String sub = file.substring(currInd);
		int endind = sub.indexOf("</ul>");
		sub = sub.substring(0, endind);
		int lastIndex= 0;
		Set<String> langSet = new HashSet<String>();
		String str = "title=";
		while(lastIndex!=-1){
			lastIndex = sub.indexOf(str);
			lastIndex+=7;
			sub = sub.substring(lastIndex);
			int in = sub.indexOf("\"");
			String lang = sub.substring(0,in);
			langSet.add(lang);
			lastIndex = sub.indexOf(str);
		}
		
		return langSet;
	}
	private Address findAd(String file){
		Address myAddress= new Address();
		int index = file.indexOf("Street Address");
		System.out.println(index);
		String sub = file.substring(index);
		index=sub.indexOf("sfx-text \">");
		sub=sub.substring(index);
		index = sub.indexOf("\n");
		index +=1;
		sub=sub.substring(index);
		if(sub.indexOf("br")==-1 || sub.indexOf("br")>sub.indexOf("\n")){
			myAddress.setLine1(sub.substring(0, sub.indexOf("\n")).trim());

		}
		else{
			index = sub.indexOf("<br>");
			myAddress.setLine1(sub.substring(0,index).trim());
			sub = sub.substring(index+4);
			myAddress.setLine2(sub.substring(0,sub.indexOf("\n")).trim());
		}
		index = sub.indexOf("ality\">");
		index+=7;
		sub = sub.substring(index);
		myAddress.setCity(sub.substring(0, sub.indexOf(",")));
		index = sub.indexOf("Region\">");
		index +=8;
		myAddress.setState(USState.fromValue(sub.substring(0, sub.indexOf("<"))));
		
		index = sub.indexOf("Code");
		index += 6;
		sub = sub.substring(index);
		myAddress.setPostalCode(sub.substring(0,sub.indexOf("<")));
		return myAddress;
		

		
		
	}
	
	private ArrayList<String> office(String file){
		int index = file.indexOf("Office Phone");
		String sub = file.substring(index);
		index = sub.indexOf("span");
		index+=5;
		ArrayList<String> myList = new ArrayList<String>();
		sub = sub.substring((index));
		myList.add(sub.substring(0, sub.indexOf("<")));
		index = sub.indexOf("openingHours");
		sub = sub.substring(index);
		index = sub.indexOf(">")+1;
		sub=sub.substring(index);
		
		while (index!=-1){
			index = sub.indexOf("openingHours");
			if (index!=-1){
				sub = sub.substring(index);
				index = sub.indexOf(">")+1;
				sub=sub.substring(index);
				myList.add(sub.substring(0,sub.indexOf("<")));
				
			}
		}
		nextOffice = file.indexOf(sub);
		
		return myList;
		
	}
	private ArrayList<Office> getOffices(String file){
		int index = file.indexOf("span5");
		ArrayList<Office> officeList = new ArrayList<Office>();
		String sub = file;

		while(index !=-1){

			Office myOf = new Office();
			sub = sub.substring(index);
		
			myOf.setLanguages(getLang(sub));
			myOf.setAddress(findAd(sub));
			ArrayList<String> phoneandhours = office(sub);
			
			myOf.setPhoneNumber(phoneandhours.get(0));
			phoneandhours.remove(0);
			myOf.setOfficeHours(phoneandhours);
			
			officeList.add(myOf);
			sub = sub.substring(2);
			index = sub.indexOf("span5");
			sub = sub.substring(index+1);
			index = sub.indexOf("span5");
			if(index ==-1){
				break;
			}
			System.out.println("My Index" + index);
			
		}
		return officeList;
	}
}
