package com.statefarm.codingcomp.agent;

import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.bean.USState;
import com.statefarm.codingcomp.utilities.SFFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
		if(firstName.isEmpty()){
			return sfFileReader.findAgentFiles()
					.stream()
					.filter(s -> s.substring(s.lastIndexOf(File.separator) + 1).toLowerCase().split("-")[1].equals(lastName.toLowerCase()))
					.map(s -> agentParser.parseAgent(s))
					.collect(Collectors.toList());
		}else if(lastName.isEmpty()){
			return sfFileReader.findAgentFiles()
					.stream()
					.filter(s -> s.substring(s.lastIndexOf(File.separator) + 1).toLowerCase().split("-")[0].equals(firstName.toLowerCase()))
					.map(s -> agentParser.parseAgent(s))
					.collect(Collectors.toList());
		}

		return sfFileReader.findAgentFiles()
				.stream()
				.filter(s -> s.substring(s.lastIndexOf(File.separator) + 1).toLowerCase().startsWith(firstName.toLowerCase() + "-" + lastName.toLowerCase()))
				.map(s -> agentParser.parseAgent(s))
				.collect(Collectors.toList());
	}

	/**
	 * Find agents by state.
	 *
	 * @param state USState
	 * @return List a list of agents associated by a state
	 */
	public List<Agent> getAgentsByState(USState state) {
		assert state != null;
		List<Agent> agents = new LinkedList<>();

		for (Agent agent : getAllAgents()) {
			try {
				if (agent.getOffices().get(0).getAddress().getState() == state) {
					agents.add(agent);
				}
			} catch (NullPointerException ex) {
				continue;
			}
		}

		/* sfFileReader.findAgentFiles()
				.stream()
				.filter(s -> s.contains(String.format("US%s%s%s", File.separator, state.toString(), File.separator)))
				.forEach(a -> agents.add(agentParser.parseAgent(a))); */

		return agents;
	}

	/**
	 * Returns a list of all Agents
	 *
	 * @return List a list of all agents
	 */
	public List<Agent> getAllAgents() {
		List<Agent> agents = new LinkedList<>();

		sfFileReader.findAgentFiles().forEach(a -> agents.add(agentParser.parseAgent(a)));

		return agents;
	}

	/**
	 * Returns a map of all agents, sorted by the unique first name
	 *
	 * @return Map sorted map of agents by unique full name
	 */
	public Map<String, List<Agent>> getAllAgentsByUniqueFullName() {
		Map<String, List<Agent>> map = new HashMap<>();
		for (Agent agent : getAllAgents()) {
			map.compute(agent.getName(), (s, agents) -> {
                if(agents == null){
	                agents = new ArrayList<>();
                }

                agents.add(agent);
                return agents;
            });
		}
		return map;
	}

	/**
	 * Returns the most popular first name
	 *
	 * @return String the most popular first name
	 */
	public String mostPopularFirstName() {
		return popularFinder(0);
	}

	/**
	 * Returns the most popular last name
	 *
	 * @return String the most popular last name
	 */
	public String mostPopularLastName() {
		return popularFinder(-1);
	}

	/**
	 * Returns the most popular suffix
	 * <p>Will either return jr or sr</p>
	 *
	 * @return String the most popular suffix
	 */
	public String mostPopularSuffix() {
		return popularFinder(-2);
	}


	/**
	 * Finds the most popular item
	 * <p>The arrayindex parameter specifies which specific data to extract from an agent.<br>
	 *     arrayIndex of 0 represents a first name, -1 represents a last name and -2 represents a suffix.</p>
	 *
	 * @param arrayIndex int the parameter which represents
	 * @return
	 */
	private String popularFinder(int arrayIndex) {
		List<Agent> agents = getAllAgents();
		HashMap<String, Integer> agentMap = new HashMap<>();

		for (Agent agent : agents) {
			String split[] = agent.getName().split(" ");
			String variable = split[arrayIndex >= 0 ? arrayIndex : split.length - 1];
			String check = variable.toLowerCase();

			if (check.contains("jr") || check.contains("sr")) {
				if (arrayIndex == -1) {
					variable = split[split.length - 2];
				}
			} else {
				if (arrayIndex == -2) {
					continue;
				}
			}

			agentMap.compute(variable, (k, v) -> (v == null) ? 1 : v + 1);
		}
		String firstName = null;
		int usage = 0;
		for (Map.Entry<String, Integer> entry : agentMap.entrySet()) {
			if (entry.getValue() > usage) {
				firstName = entry.getKey();
				usage = entry.getValue();
			}
		}

		return firstName;
	}

}
