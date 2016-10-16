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
			
			String[] aName = a.getName().split(" ");
			if ((aName[0].equals(firstName) || firstName == "") && (aName[1].equals(lastName) || lastName == ""))
				AgentsByName.add(a);
			
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
		
		for (Agent a: all) {
			if (a.getOffices().get(0).getAddress().getState() == state)
				AgentsByState.add(a);
		}
		
		return AgentsByState;
	}

	public List<Agent> getAllAgents() {
		List<Agent> allAgents = new ArrayList<Agent>();
		// I'm assuming sfFileReader somehow gets filenames; then I'm getting all the agents with agentParser
		for (String fileName: sfFileReader.findAgentFiles()){
			allAgents.add(agentParser.parseAgent(fileName));	
		}
		return allAgents;
	} 

	public Map<String, List<Agent>> getAllAgentsByUniqueFullName() {
		
		HashMap names = new HashMap<String, List<Agent>>();
		List<Agent> all = getAllAgents();
		for (Agent a: all) {
			if (!names.containsKey(a.getName())) {
				names.put(a.getName(), new ArrayList<Agent>());
			}
			((ArrayList<Agent>)names.get(a.getName())).add(a);
		}
		return names;
	}

    /*public String mostPopularFirstName() {
		
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

	}*/
    
    public String mostPopularFirstName() {
		List<Agent> all = getAllAgents();
		HashMap<String, Integer> firstNameCount = new HashMap<String, Integer>();
		String[] pieces;
		String firstName;
		for (Agent a: all) {
			pieces = a.getName().split(" ");
			firstName = pieces[0];
			if (!firstNameCount.containsKey(firstName)) {
				firstNameCount.put(firstName, 1);
			} else {
				firstNameCount.put(firstName, firstNameCount.get(firstName)+1);
			}
		}
		String best="";
		int maxCount = -1;
		for (String s: firstNameCount.keySet()) {
			if (firstNameCount.get(s) > maxCount) {
				maxCount = firstNameCount.get(s);
				best = s;
			}
		}
		return best;
	}

    

	public String mostPopularLastName() {
		List<Agent> all = getAllAgents();
		HashMap<String, Integer> lastNameCount = new HashMap<String, Integer>();
		String[] pieces;
		String lastName;
		for (Agent a: all) {
			pieces = a.getName().split(" ");
			lastName = pieces[1];
			if (!lastNameCount.containsKey(lastName)) {
				lastNameCount.put(lastName, 1);
			} else {
				lastNameCount.put(lastName, lastNameCount.get(lastName)+1);
			}
		}
		String best="";
		int maxCount = -1;
		for (String s: lastNameCount.keySet()) {
			if (lastNameCount.get(s) > maxCount) {
				maxCount = lastNameCount.get(s);
				best = s;
			}
		}
		return best;
	}

	public String mostPopularSuffix() {
		List<Agent> all = getAllAgents();
		HashMap<String, Integer> suffixCount = new HashMap<String, Integer>();
		String[] pieces;
		String suffix;
		for (Agent a: all) {
			pieces = a.getName().split(" ");
			if (pieces.length == 3) {
				suffix = pieces[2];
				if (!suffixCount.containsKey(suffix)) {
					suffixCount.put(suffix, 1);
				} else {
					suffixCount.put(suffix, suffixCount.get(suffix)+1);
				}
			}
		}
		String best="";
		int maxCount = -1;
		for (String s: suffixCount.keySet()) {
			if (suffixCount.get(s) > maxCount) {
				maxCount = suffixCount.get(s);
				best = s;
			}
		}
		return best;
	}
}
