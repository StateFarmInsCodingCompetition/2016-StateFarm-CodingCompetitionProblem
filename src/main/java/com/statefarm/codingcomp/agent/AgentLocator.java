package com.statefarm.codingcomp.agent;

import java.util.*;
// import java.util.List;
// import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.statefarm.codingcomp.bean.Agent;
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
		
		// AgentParser ag = new AgentParser();
		
		List<Agent> all = getAllAgents();
		
		List<Agent> AgentsByName = new ArrayList<Agent>();
		
		// I'm assuming sfFileReader somehow gets filenames; then I'm getting all the agents with agentParser
		for(Agent a: all){
			
			// what's last name??
			if(a.getName().equals(firstName) && ){
				AgentsByName.add(a);
			}
			
		}
		
		return AgentsByName;
	}

	/**
	 * Find agents by state.
	 * 
	 * @param state
	 * @return
	 */
	public List<Agent> getAgentsByState(USState state) {
		// AgentParser ag = new AgentParser();
		List<Agent> AgentsByState = new ArrayList<Agent>();
		
		List<Agent> all = getAllAgents();
		
		
		
		return AgentsByState;
	}

	public List<Agent> getAllAgents() {
		AgentParser ag = new AgentParser();
		List<Agent> allAgents = new ArrayList<Agent>();
		
		// I'm assuming sfFileReader somehow gets filenames; then I'm getting all the agents with agentParser
		for(fileName: ????){
			allAgents.add(ag.parseAgent(fileName));	
		}
		
		return allAgents;
	}

	public Map<String, List<Agent>> getAllAgentsByUniqueFullName() {
		
		HashMap names = new HashMap<String, List<Agent>>();
		
		return null;
	}

	public String mostPopularFirstName() {
		
		HashMap<String, Integer> nameN = new HashMap<String, Integer>();
		
		List<Agent> all = getAllAgents();
		// Where do the names come from?
		for(Agent a: all){
			if(nameN.containsKey(a.getName())){
				nameN.put(a.getName(), nameN.get(a.getName())+1);
			}else{
				nameN.put(a.getName(), 1);
			}
		}
		
		// add exception if it's empty?
		String mostPopular = nameN.keySet().iterator().next();
		
		for(String k: nameN.keySet()){
			if(nameN.get(k) > nameN.get(mostPopular)){
				mostPopular = k;
			}
		}
		
		return mostPopular;

	}

	public String mostPopularLastName() {
		return null;

	}

	public String mostPopularSuffix() {
		return null;

	}
}
