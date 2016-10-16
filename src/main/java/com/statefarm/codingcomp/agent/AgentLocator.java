package com.statefarm.codingcomp.agent;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		List<String> agents = sfFileReader.findAgentFiles();
		List<Agent> agentList = new ArrayList<Agent>();
		for (String agent : agents) {
			String name = agentParser.parseAgent(agent).getName();
			if (name.equals(firstName.concat("-")) || name.equals("-".concat(lastName))) {
				agentList.add(agentParser.parseAgent(agent));
			}
		}
		return agentList;
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
		Hashtable<String, Integer> count = new Hashtable<String, Integer>();
		List<String> agents = sfFileReader.findAgentFiles();
		List<Agent> agentList = new ArrayList<Agent>();
		int max = 0;
		for (String agent : agents) {
			String name = agentParser.parseAgent(agent).getName();
			if (count.contains(name)) {
				count.put(name, count.get(name) + 1);
			} else {
				count.put(name, 1);
			}
			if (count.get(name) > max) {
				max = count.get(name);
			}
		}
		Set<String> keyset = count.keySet();
		for (String k : keyset) {
			if (count.get(k) == max) {
				return k;
			}
		}
		return null;
	}

	public String mostPopularLastName() {
		return null;

	}

	public String mostPopularSuffix() {
		return null;

	}
}
