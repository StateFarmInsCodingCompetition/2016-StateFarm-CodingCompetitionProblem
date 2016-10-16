package com.statefarm.codingcomp.agent;

import java.util.ArrayList;
import java.util.HashMap;
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
		return getAgentsByName(firstName, lastName, "");
	}
	
	public List<Agent> getAgentsByName(String firstName, String lastName, String suffix) {
		List<Agent> agents = getAllAgents();
		for(int i=0; i<agents.size();){
			String name = agents.get(i).getName();
			if(name.contains(firstName) && name.contains(lastName) && name.contains(suffix)){
				i++;
			}else{
				agents.remove(i);
			}
		}
		return agents;
	}

	/**
	 * Find agents by state.
	 * 
	 * @param state
	 * @return
	 */
	public List<Agent> getAgentsByState(USState state) {
		List<Agent> agents = getAllAgents();
		for(int i=0; i<agents.size();){
			if(agents.get(i).hasOfficeIn(state)){
				i++;
			}else{
				agents.remove(i);
			}
		}
		return agents;
	}

	public List<Agent> getAllAgents() {
		return agentParser.getAllAgents();
	}

	public Map<String, List<Agent>> getAllAgentsByUniqueFullName() {
		Map<String, List<Agent>> agents = new HashMap<String, List<Agent>>();
		for(Agent a : getAllAgents()){
			if(!agents.containsKey(a.getName())){
				agents.put(a.getName(), new ArrayList<Agent>());
			}			
			agents.get(a.getName()).add(a);
		}
		return agents;
	}

	public String mostPopularFirstName() {
		return getMostPopularName(0);
	}

	public String mostPopularLastName() {
		return getMostPopularName(1);
	}

	public String mostPopularSuffix() {
		return getMostPopularName(2);
	}
	
	private String getMostPopularName(int nameIndex /*ie first name is 0, last is 1, and suffix is 2*/){
		Map<String, Integer> nameCount = new HashMap<String, Integer>();
		
		//count all the names
		for(String[] name : getAllNames()){
			String subName = nameIndex>=name.length ? "" : name[nameIndex];
			if(subName.isEmpty()) continue;
			
			if(nameCount.containsKey(subName)){
				nameCount.put(subName, nameCount.get(subName)+1);
			}else{
				nameCount.put(subName, 1);
			}
		}
		
		//find the one with the greatest count (or one of the ones with the greatest count)
		String max = null;
		for(String name : nameCount.keySet()){
			if(max==null || nameCount.get(max)<nameCount.get(name)){
				max = name;
			}
		}
		
		return max;
	}
	
	private List<String[]> getAllNames(){
		List<String[]> names = new ArrayList<String[]>();
		for(Agent a : getAllAgents()){
			String[] name = a.getName().split(" ");
			for(int i=0; i<name.length; i++) name[i] = name[i].trim().replace(",", "");
			names.add(name);
		}
		return names;
	}
}
