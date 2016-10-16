package com.statefarm.codingcomp.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.bean.Office;
import com.statefarm.codingcomp.bean.USState;
import com.statefarm.codingcomp.utilities.SFFileReader;

/**
 * Only US Agents are applicable since State Farm is currently in the process of
 * selling off its Canadian business.
 */
@Component
public class AgentLocator {
	@Autowired
	private AgentParser agentParser;

	@Autowired
	private SFFileReader sfFileReader;
	
	
	/**
	 * Find agents where the URL of their name contains the firstName and
	 * lastName For instance, Tom Newman would search for "Tom-" and "-Newman"	
	 * in the URL.
	 * 
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public List<Agent> getAgentsByName(String firstName, String lastName) {
		List<String> myList = sfFileReader.findHtmlFiles();
		List<Agent> myAgents = new ArrayList<Agent>();
		for(String str: myList) {
		    if((str.trim().contains(firstName + "-")) && (str.trim().contains("-" + lastName))){
		    	myAgents.add(agentParser.parseAgent(str));
		    }
		}
		return myAgents;
	}

	/**
	 * Find agents by state.
	 * 
	 * @param state
	 * @return a list of agents who operate in the state given by state 
	 */
	public List<Agent> getAgentsByState(USState state) {
		List<String> agentHtmlFiles = sfFileReader.findAgentFiles();
		List<Agent> allAgents = getAllAgents();
        List<Agent> agentsByState = new ArrayList<Agent>();
        
        for(Agent a: allAgents) {
            for (Office o : a.getOffices()) {
        		if (o.getAddress().getState() == state) {
        			agentsByState.add(a);
        			continue;
        		}
        	}
        }
        return agentsByState;
	}

	public List<Agent> getAllAgents() {
		List<String> agentFiles = sfFileReader.findAgentFiles();
		List<Agent> agents = new ArrayList<Agent>();
		for (String str: agentFiles) {
		    agents.add(agentParser.parseAgent(str));
		}
		return agents;
	}
	
	public Map<String, List<Agent>> getAllAgentsByUniqueFullName() {
        List<Agent> allAgents = getAllAgents();
        Map<String, List<Agent>> map = new HashMap<String, List<Agent>>();
        for(Agent a: allAgents) {
             if(map.containsKey(a.getName())) {
                 map.get(a.getName()).add(a);
             } else {
                 List<Agent> list = new ArrayList<Agent>();
                 list.add(a);
                 map.put(a.getName(), list);
             }
        }
        return map;
	}

	public String mostPopularFirstName() {
		List<Agent> allAgents = getAllAgents();
		Map<String, Integer> map = new HashMap<String, Integer>();
        for (Agent a : allAgents) {
		     String firstname = a.getName().trim().split(" ")[0];
		     if (map.containsKey(a.getName().trim().split(" ")[0])) {
		         map.put(firstname, map.get(firstname) + 1);
		     } else {
		         map.put(firstname, 1);
		     }
		 }
		 String maxEntry = null;
		
		 for (String entry : map.keySet())
		 {
		     if (maxEntry == null || map.get(entry).compareTo(map.get(maxEntry)) > 0) {
		         maxEntry = entry;
		     }
		 }
		return maxEntry;
	}

	public String mostPopularLastName() {
		List<Agent> allAgents = getAllAgents();
		Map<String, Integer> map = new HashMap<String, Integer>();
        for (Agent a : allAgents) {
		     String lastname = a.getName().trim().split(" ")[1];
		     if (map.containsKey(a.getName().trim().split(" ")[1])) {
		         map.put(lastname, map.get(lastname) + 1);
		     } else {
		         map.put(lastname, 1);
		     }
		 }
		 String maxEntry = null;
		
		 for (String entry : map.keySet())
		 {
		     if (maxEntry == null || map.get(entry).compareTo(map.get(maxEntry)) > 0) {
		         maxEntry = entry;
		     }
		 }
		return maxEntry;

	}

	public String mostPopularSuffix() {
		List<Agent> allAgents = getAllAgents();
		Map<String, Integer> map = new HashMap<String, Integer>();
        for (Agent a : allAgents) {
		     String[] suffixList = a.getName().trim().split(" ");
		     if (suffixList.length >= 3) {
		    	 String suffix = suffixList[2];
		    	 if (map.containsKey(a.getName().trim().split(" ")[2])) {
		    		 map.put(suffix, map.get(suffix) + 1);
		    	 } else {
		    		 map.put(suffix, 1);
		    	 }
		     }
		 }
		 String maxEntry = null;
		
		 for (String entry : map.keySet())
		 {
		     if (maxEntry == null || map.get(entry).compareTo(map.get(maxEntry)) > 0) {
		         maxEntry = entry;
		     }
		 }
		return maxEntry;
	}
}
