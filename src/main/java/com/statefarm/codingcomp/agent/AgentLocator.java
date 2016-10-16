package com.statefarm.codingcomp.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		this.sfFileReader = new SFFileReader();
		this.agentParser = new AgentParser();
		List<Agent> agentsThatMatchName = new ArrayList<Agent>();
		List<String> agentDirectories = sfFileReader.findAgentFiles();
		String urlName = firstName + "-" + lastName;
		for(String agentdirectory: agentDirectories){
			if (agentdirectory.indexOf(urlName) > 0){
				agentsThatMatchName.add(agentParser.parseAgent(agentdirectory));				
			}				
		}
		
		return agentsThatMatchName;
		
	}

	/**
	 * Find agents by state.
	 * 
	 * @param state
	 * @return
	 */
	public List<Agent> getAgentsByState(USState state) {
		return null;
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
	
	public static void main(String[] args){
		SFFileReader sfFileReader = new SFFileReader();
		List<String> agents = sfFileReader.findAgentFiles();
		System.out.println(agents);
	}
}
