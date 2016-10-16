package com.statefarm.codingcomp.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private List<String> htmlFiles = sfFileReader.findHtmlFiles();
	private List<String> agentFiles = sfFileReader.findAgentFiles();
	
	private Map<String, Agent> agentsByHTML = getAgentsByHTML();
	private Map<String, Agent> agentsByAgentFiles = getAgentsByAgentFiles();
	
	private Map<String, Agent> getAgentsByHTML() {
		Map<String, Agent> agents = new HashMap<String, Agent>();
		for (String html : htmlFiles) {
			agents.put(html, agentParser.parseAgent(html));
		}
		return agents;
	}
	
	private Map<String, Agent> getAgentsByAgentFiles() {
		Map<String, Agent> agents = new HashMap<String, Agent>();
		for (String html : agentFiles) {
			agents.put(html, agentParser.parseAgent(html));
		}
		return agents;
	}
	
	
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
		List<Agent> myAgents = new ArrayList<Agent>();
		for (String html : htmlFiles) {
		    if ((html.trim().contains(firstName + "-")) && (html.trim().contains("-" + lastName))){
		    	myAgents.add(agentsByHTML.get(html));
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
		return null;
	}
	
	public Map<String, List<Agent>> getAllAgentsByUniqueFullName() {
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
