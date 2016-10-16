package com.statefarm.codingcomp.agent;

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
		return null;
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
}
