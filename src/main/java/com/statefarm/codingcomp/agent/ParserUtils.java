package com.statefarm.codingcomp.agent;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public final class ParserUtils {

	private ParserUtils(){}


	//these are methods for getting the text in  the leaf level tags of an element so that I don't have to iterate over everything every time
	
	public static List<String> getTextFieldsUnderElementWithItemprop(String itemprop, Element element){
		return getTextFieldsUnderElements(element.getElementsByAttributeValue("itemprop", itemprop));
	}
	
	public static List<String> getTextFieldsUnderElement(Element element){
		List<String> fields = new ArrayList<String>();
		
		if(element.children().isEmpty()){
			fields.add(element.text());
		}else{
			//otherwise, recurse
			for(Element n : element.children()){
				fields.addAll(getTextFieldsUnderElement(n));
			}
		}		
		
		return fields;
	}
	
	public static List<String> getTextFieldsUnderNode(Node element){
		List<String> fields = new ArrayList<String>();
		
		if(element.childNodes().isEmpty() && !element.toString().trim().isEmpty()){
			fields.add(element.toString());
		}else{
			//otherwise, recurse
			for(Node n : element.childNodes()){
				fields.addAll(getTextFieldsUnderNode(n));
			}
		}		
		
		return fields;
	}
	
	
	public static List<String> getTextFieldsUnderElements(Elements elements){
		List<String> fields = new ArrayList<String>();
		
		for(Element e : elements){
			fields.addAll(getTextFieldsUnderElement(e));
		}
		
		return fields;
	}
	
}
