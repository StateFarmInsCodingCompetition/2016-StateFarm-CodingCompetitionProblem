package com.statefarm.codingcomp.agent;

import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.bean.USState;
import com.statefarm.codingcomp.utilities.SFFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

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
	 * in the URL. CASE INSENSITIVE
	 * 
	 * @param firstName agent first name
	 * @param lastName agent last name
	 * @return A list of agents with the provided first name and last name
	 */
	public List<Agent> getAgentsByName(String firstName, String lastName) {
        //convert everything to lowercase to be case insensitive
        firstName = firstName.toLowerCase();
        lastName = lastName.toLowerCase();

		List<String> agentFiles = sfFileReader.findAgentFiles(); //get a list of agent files

		List<Agent> matchedAgents = new ArrayList<>();

		for (String agentFile : agentFiles) { //loop through all agent files
			String[] pathParts = agentFile.split(Pattern.quote(File.separator)); //split the filename into parts based on system path separator
			String fileName = pathParts[pathParts.length-1]; //get the last part (the actual file name)
			fileName = fileName.split("\\.")[0]; //get rid of .html extension
            fileName = fileName.toLowerCase(); //case insensitive

            //check to see if it contains the requested first and last name -- if so, add to list
			if (fileName.contains(firstName+"-") && fileName.contains("-"+lastName)) {
				matchedAgents.add(agentParser.parseAgent(agentFile));
			}
		}
		return matchedAgents;
	}

	/**
	 * Find agents by state.
	 * 
	 * @param state the state to get agents of
	 * @return a list of agents in the state
	 */
	public List<Agent> getAgentsByState(USState state) {
		List<String> agentFiles = sfFileReader.findAgentFiles();

		List<Agent> matchedAgents = new ArrayList<>();
		for (String agentFile : agentFiles) {
			String[] pathParts = agentFile.split(Pattern.quote(File.separator));
            //get the 3rd to last path
            System.out.println(Arrays.toString(pathParts));
            String agentState = pathParts[pathParts.length-3];

			if (agentState.equals(state.name())) {
				matchedAgents.add(agentParser.parseAgent(agentFile));
			}
		}
		return matchedAgents;
	}

	/**
	 * Get a list of all agents
	 * @return a list of all agents
	 */
	public List<Agent> getAllAgents() {
		List<String> agentFiles = sfFileReader.findAgentFiles();

		List<Agent> agents = new ArrayList<>();
		for (String agentFile : agentFiles) {
			agents.add(agentParser.parseAgent(agentFile));
		}
		return agents;
	}

	/**
	 * Get a map of agent first and last name to a list of agents with that first and last name
	 * @return a map of agent first and last name to a list of agents with that first and last name
	 */
	public Map<String, List<Agent>> getAllAgentsByUniqueFullName() {
		List<Agent> agents = getAllAgents();
		Map<String, List<Agent>> nameMap = new HashMap<>();

		for (Agent agent : agents) {
			String fullName = agent.getName();

			//if they are already in the map, add them to the list
			if (nameMap.containsKey(fullName)) {
				nameMap.get(fullName).add(agent);
			}
			else {
				//otherwise, create a new list with their name in it, and add to map
				List<Agent> agentList = new ArrayList<>();
				agentList.add(agent);
				nameMap.put(fullName, agentList);
			}
		}
		return nameMap;
	}

	/**
	 * Get the most popular first name
	 * @return the most popular agent first name
	 */
	public String mostPopularFirstName() {
		List<Agent> agents = getAllAgents();

		Map<String, Integer> counter = new HashMap<>();
		for (Agent agent : agents) {
			String firstName = agent.getFirstName();
            if (firstName != null) {
                if (counter.containsKey(firstName)) {
                    counter.put(firstName, counter.get(firstName)+1);
                }
                else {
                    counter.put(firstName, 1);
                }
            }
		}

		String mostPopular = null;
		int mostPopularAmt = Integer.MIN_VALUE;

		for (Map.Entry<String, Integer> e : counter.entrySet()) {
			if (e.getValue() > mostPopularAmt) {
				mostPopularAmt = e.getValue();
				mostPopular = e.getKey();
			}
		}
		return mostPopular;
	}

	/**
	 * Get the most popular agent last name
	 * @return the most popular agent last name
	 */
	public String mostPopularLastName() {
		List<Agent> agents = getAllAgents();

		Map<String, Integer> counter = new HashMap<>();
		for (Agent agent : agents) {
			String lastName = agent.getLastName();

            if (lastName != null) {
                if (counter.containsKey(lastName)) {
                    counter.put(lastName, counter.get(lastName)+1);
                }
                else {
                    counter.put(lastName, 1);
                }
            }
		}

		String mostPopular = null;
		int mostPopularAmt = Integer.MIN_VALUE;

		for (Map.Entry<String, Integer> e : counter.entrySet()) {
			if (e.getValue() > mostPopularAmt) {
				mostPopularAmt = e.getValue();
				mostPopular = e.getKey();
			}
		}
		return mostPopular;
	}

	/**
	 * Get the most popular agent suffix
	 * @return the most popular suffix in an agent name
	 */
	public String mostPopularSuffix() {
        List<Agent> agents = getAllAgents();

        Map<String, Integer> counter = new HashMap<>();
        for (Agent agent : agents) {
            String suffix = agent.getSuffix();

            if (suffix != null) {
                if (counter.containsKey(suffix)) {
                    counter.put(suffix, counter.get(suffix)+1);
                }
                else {
                    counter.put(suffix, 1);
                }
            }
        }

        String mostPopular = null;
        int mostPopularAmt = Integer.MIN_VALUE;

        for (Map.Entry<String, Integer> e : counter.entrySet()) {
            if (e.getValue() > mostPopularAmt) {
                mostPopularAmt = e.getValue();
                mostPopular = e.getKey();
            }
        }
        return mostPopular;
	}
}
