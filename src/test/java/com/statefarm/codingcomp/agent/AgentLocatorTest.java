package com.statefarm.codingcomp.agent;

import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.bean.USState;
import com.statefarm.codingcomp.configuration.CodingCompetitionConfiguration;
import com.statefarm.codingcomp.utilities.SFFileReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CodingCompetitionConfiguration.class)

public class AgentLocatorTest {
	@Autowired
	private AgentLocator agentLocator;

	@Autowired
	private SFFileReader sfFileReader;
	
	private static final Gson GSON = new Gson();
	private static final Type AGENT_LIST_TYPE = new TypeToken<List<Agent>>() {
	}.getType();

	
	@Test
	public void canFindSingleAgentByFirstAndLastName() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		List<Agent> expectedAgents = readAgents("MaryContrerasExpected.json");
		List<Agent> actualAgents = agentLocator.getAgentsByName("Mary", "Contreras");

		assertLenientEquals(expectedAgents, actualAgents);
	}
	
	@Test
	public void canFindMultipleAgentsByLastName() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		List<Agent> expectedAgents = readAgents("MultipleAgentsLastNameStokesExpected.json");
		List<Agent> actualAgents = agentLocator.getAgentsByName("", "Stokes");

		assertLenientEquals(expectedAgents, actualAgents);
	}
	
	@Test
	public void canFindMultipleAgentsByFirstName() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		List<Agent> expectedAgents = readAgents("MultipleAgentsFirstNameJoshExpected.json");
		List<Agent> actualAgents = agentLocator.getAgentsByName("Josh", "");

		assertLenientEquals(expectedAgents, actualAgents);
	}
	
	@Test
	public void canFindAllUtahAgentsCount() {
		assertEquals(182, agentLocator.getAgentsByState(USState.UT).size());
	}
	
	@Test
	public void canGetAllAgentsCount() {
		assertEquals(1246, agentLocator.getAllAgents().size());
	}
	
	@Test
	public void canGetAllAgentsByUniqueFullName() {
		assertEquals(1226, agentLocator.getAllAgentsByUniqueFullName().size());
	}
	
	@Test
	public void canGetAgentMostPopularFirstName() {
		assertEquals("Mike", agentLocator.mostPopularFirstName());
	}
	
	@Test
	public void canGetAgentMostPopularLastName() {
		assertEquals("Johnson", agentLocator.mostPopularLastName());
	}
	
	@Test
	public void canGetAgentMostPopularSuffix() {
		assertEquals("Jr", agentLocator.mostPopularSuffix());
	}
	
	
	private List<Agent> readAgents(String filename) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		return GSON.fromJson(sfFileReader.readFile(Paths.get("src", "test", "resources", filename).toString()), AGENT_LIST_TYPE);
	}
}
