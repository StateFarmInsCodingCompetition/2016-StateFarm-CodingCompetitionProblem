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
		List<Agent> AgentsByName = new ArrayList<Agent>();
		
		// I'm assuming sfFileReader somehow gets filenames; then I'm getting all the agents with agentParser
		for(fileName: ????){
			Agent a = agentParser.parseAgent(fileName);
			
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
		List<Agent> AgentsByState = new ArrayList<Agent>();
		
		return AgentsByState;
	}

	public List<Agent> getAllAgents() {
		List<Agent> allAgents = new ArrayList<Agent>();
		
		// I'm assuming sfFileReader somehow gets filenames; then I'm getting all the agents with agentParser
		for(fileName: ????){
			allAgents.add(agentParser.parseAgent(fileName));	
		}
		
		return allAgents;
	}

	public Map<String, List<Agent>> getAllAgentsByUniqueFullName() {
		
		HashMap names = new HashMap<String, List<Agent>>();
		
		return null;
	}

	public String mostPopularFirstName() {
		return null;

	}

	public String mostPopularLastName() {
		return null;

	}

	public String mostPopularSuffix() {
		return null;

	}
}
