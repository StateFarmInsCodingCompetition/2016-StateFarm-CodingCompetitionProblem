package com.statefarm.codingcomp.agent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.utilities.SFFileReader;

@Component
public class AgentParser {
	
	@Autowired
	private SFFileReader sfFileReader;

	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
		
		//retrieve the file given
		File file = new File(fileName);
		
		//parse the webpage into a Jsoup Document object
		Document page;
		try{
			page = Jsoup.parse(file, "UTF-8");//assume the charset is UTF-8, maybe I'll look into making this more dynamic later.
		}catch(IOException e){
			//something went wrong so just return null.
			e.printStackTrace();
			return null;
		}
		
		//create a new agent object and parse the document data
		Agent a = new Agent();
		a.parse(page);
		
		return a;
	}
	
	public List<Agent> getAllAgents(){
		List<Agent> agents = new ArrayList<Agent>();
		for(String s : sfFileReader.findAgentFiles()){
			Agent a = parseAgent(s);
			agents.add(a);
		}
		System.out.println("\n");
		return agents;
	}
	
}
