package com.statefarm.codingcomp.agent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.statefarm.codingcomp.bean.Address;
import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.bean.Office;
import com.statefarm.codingcomp.bean.Product;
import com.statefarm.codingcomp.bean.USState;
import com.statefarm.codingcomp.configuration.CodingCompetitionConfiguration;
import com.statefarm.codingcomp.utilities.SFFileReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CodingCompetitionConfiguration.class)
public class AgentParserTest {
	@Autowired
	private AgentParser agentParser;
		
	@Autowired
	private SFFileReader sFFileReader;
	
	private String kevinParksWebsitePath;
	private String debbiePeckWebsitePath;
	private Agent expectedAgent1;
	private Agent expectedAgent2;
	private Agent actualAgent1;
	private Agent actualAgent2;
	
	@Before
	public void setup() {
		kevinParksWebsitePath = Paths.get("src", "test", "resources", "KevinParks.html").toString();
		debbiePeckWebsitePath = Paths.get("src", "test", "resources", "DebbiePeck.html").toString();
		
		expectedAgent1 = createExpectedAgent1();
		actualAgent1 = agentParser.parseAgent(kevinParksWebsitePath);
		
		expectedAgent2 = createExpectedAgent2();
		actualAgent2 = agentParser.parseAgent(debbiePeckWebsitePath);
	}
	

	@Test
	public void canLoadAllProducts() {

		assertEquals(expectedAgent1.getProducts(), actualAgent1.getProducts());
	}
	
	@Test
	public void canLoadPartialProducts() {
		
		assertEquals(expectedAgent2.getProducts(), actualAgent2.getProducts());
	}
	
	@Test
	public void canParseEntireAgent() {
		
		assertLenientEquals(expectedAgent1, actualAgent1);
		assertLenientEquals(expectedAgent2, actualAgent2);
	}
	
	@Test
	public void canParseAgentName() {
		
		assertEquals(expectedAgent1.getName(), actualAgent1.getName());
		assertEquals(expectedAgent2.getName(), actualAgent2.getName());
	}
	
	@Test
	public void canParseAgentPrimaryOfficeFirstLanguage() {
		
		assertTrue(actualAgent1.getOffices().get(0).getLanguages().contains("English"));
		assertTrue(actualAgent2.getOffices().get(0).getLanguages().contains("English"));
	}
	
	@Test
	public void canParseAgentPrimaryOfficeAllLanguages() {
		
		assertEquals(expectedAgent1.getOffices().get(0).getLanguages(), actualAgent1.getOffices().get(0).getLanguages());
		assertEquals(expectedAgent2.getOffices().get(0).getLanguages(), actualAgent2.getOffices().get(0).getLanguages());
	}
	
	@Test
	public void canParseAgentPrimaryOfficePhone() {
		
		assertEquals(expectedAgent1.getOffices().get(0).getPhoneNumber(), actualAgent1.getOffices().get(0).getPhoneNumber());
		assertEquals(expectedAgent2.getOffices().get(0).getPhoneNumber(), actualAgent2.getOffices().get(0).getPhoneNumber());
	}
	
	@Test
	public void canParseAgentPrimaryOfficeFirstOfficeHoursLine() {
		
		assertEquals(expectedAgent1.getOffices().get(0).getOfficeHours().get(0), actualAgent1.getOffices().get(0).getOfficeHours().get(0));
		assertEquals(expectedAgent2.getOffices().get(0).getOfficeHours().get(0), actualAgent2.getOffices().get(0).getOfficeHours().get(0));
	}
	
	@Test
	public void canParseAgentPrimaryOfficeAllOfficeHoursLines() {
		
		assertEquals(expectedAgent1.getOffices().get(0).getOfficeHours(), actualAgent1.getOffices().get(0).getOfficeHours());
		assertEquals(expectedAgent2.getOffices().get(0).getOfficeHours(), actualAgent2.getOffices().get(0).getOfficeHours());
	}
	
	@Test
	public void canParseAgentPrimaryOfficeAddressLine1() {
		
		assertEquals(expectedAgent1.getOffices().get(0).getAddress().getLine1(), actualAgent1.getOffices().get(0).getAddress().getLine1());
		assertEquals(expectedAgent2.getOffices().get(0).getAddress().getLine1(), actualAgent2.getOffices().get(0).getAddress().getLine1());
	}
	
	@Test
	public void canParseAgentPrimaryOfficeAddressLine2() {
		
		assertEquals(expectedAgent1.getOffices().get(0).getAddress().getLine2(), actualAgent1.getOffices().get(0).getAddress().getLine2());
		assertEquals(expectedAgent2.getOffices().get(0).getAddress().getLine2(), actualAgent2.getOffices().get(0).getAddress().getLine2());
	}
	
	@Test
	public void canParseAgentPrimaryOfficeAddressCity() {
		
		assertEquals(expectedAgent1.getOffices().get(0).getAddress().getCity(), actualAgent1.getOffices().get(0).getAddress().getCity());
		assertEquals(expectedAgent2.getOffices().get(0).getAddress().getCity(), actualAgent2.getOffices().get(0).getAddress().getCity());
	}
	
	@Test
	public void canParseAgentPrimaryOfficeAddressState() {
		
		assertEquals(expectedAgent1.getOffices().get(0).getAddress().getState(), actualAgent1.getOffices().get(0).getAddress().getState());
		assertEquals(expectedAgent2.getOffices().get(0).getAddress().getState(), actualAgent2.getOffices().get(0).getAddress().getState());
	}
	
	@Test
	public void canParseAgentPrimaryOfficeAddressPostalCode() {
		
		assertEquals(expectedAgent1.getOffices().get(0).getAddress().getPostalCode(), actualAgent1.getOffices().get(0).getAddress().getPostalCode());
		assertEquals(expectedAgent2.getOffices().get(0).getAddress().getPostalCode(), actualAgent2.getOffices().get(0).getAddress().getPostalCode());
	}
	
	@Test
	public void canParseAgentSecondaryOfficeFirstLanguage() {

		assertTrue(actualAgent1.getOffices().get(1).getLanguages().contains("English"));
	}
	
	@Test
	public void canParseAgentSecondaryOfficeAllLanguages() {
		
		assertEquals(expectedAgent1.getOffices().get(1).getLanguages(), actualAgent1.getOffices().get(1).getLanguages());
	}
	
	@Test
	public void canParseAgentSecondaryOfficePhone() {
		
		assertEquals(expectedAgent1.getOffices().get(1).getPhoneNumber(), actualAgent1.getOffices().get(1).getPhoneNumber());
	}
	
	@Test
	public void canParseAgentSecondaryOfficeFirstOfficeHoursLine() {
		
		assertEquals(expectedAgent1.getOffices().get(1).getOfficeHours().get(0), actualAgent1.getOffices().get(1).getOfficeHours().get(0));
	}
	
	@Test
	public void canParseAgentSecondaryOfficeAllOfficeHoursLines() {
		
		assertEquals(expectedAgent1.getOffices().get(1).getOfficeHours(), actualAgent1.getOffices().get(1).getOfficeHours());
	}
	
	@Test
	public void canParseAgentSecondaryOfficeAddressLine1() {
		
		assertEquals(expectedAgent1.getOffices().get(1).getAddress().getLine1(), actualAgent1.getOffices().get(1).getAddress().getLine1());
	}
	
	@Test
	public void canParseAgentSecondaryOfficeAddressLine2() {
		
		assertEquals(expectedAgent1.getOffices().get(1).getAddress().getLine2(), actualAgent1.getOffices().get(1).getAddress().getLine2());
	}
	
	@Test
	public void canParseAgentSecondaryOfficeAddressCity() {
		
		assertEquals(expectedAgent1.getOffices().get(1).getAddress().getCity(), actualAgent1.getOffices().get(1).getAddress().getCity());
	}
	
	@Test
	public void canParseAgentSecondaryOfficeAddressState() {
		
		assertEquals(expectedAgent1.getOffices().get(1).getAddress().getState(), actualAgent1.getOffices().get(1).getAddress().getState());
	}
	
	@Test
	public void canParseAgentSecondaryOfficeAddressPostalCode() {
		
		assertEquals(expectedAgent1.getOffices().get(1).getAddress().getPostalCode(), actualAgent1.getOffices().get(1).getAddress().getPostalCode());
	}
	
	private Agent createExpectedAgent1() {
		// This agent offers all products and has multiple offices 
		// New up the agent
		Agent expectedAgent = new Agent();
		
		// Set name
		expectedAgent.setName("Kevin Parks");
		
		// Set products
		Set<Product> products = new HashSet<Product>();
		products.addAll(Arrays.asList(Product.values()));
		
		expectedAgent.setProducts(products);
		
		// New up offices (and addresses for offices) and populate
		Office office1 = new Office();
		Office office2 = new Office();
		Set<String> languages1 = new HashSet<String>();
		Set<String> languages2 = new HashSet<String>();
		List<String> office1Hours = new ArrayList<String>();
		List<String> office2Hours = new ArrayList<String>();
		Address address1 = new Address();
		Address address2 = new Address();
		
		languages1.add("English");
		languages1.add("Spanish");
		languages2.add("English");
		languages2.add("Spanish");
		office1Hours.add("Mon - Thur 9:00am to 5:00pm");
		office1Hours.add("Friday 9:00am to Noon and");
		office1Hours.add("1:30pm to 5:00pm");
		office2Hours.add("Monday-Thursday 10:00-2:00");
		office2Hours.add("Other Times By Appointment");
		office2Hours.add("Other Time");
		address1.setLine1("126 E 5th Street");
		address1.setLine2(null);
		address1.setCity("Delta");
		address1.setState(USState.CO);
		address1.setPostalCode("81416-1903");
		address2.setLine1("211 Grand Ave Suite 102");
		address2.setLine2("P.O. Box 1018");
		address2.setCity("Paonia");
		address2.setState(USState.CO);
		address2.setPostalCode("81428");
		
		office1.setLanguages(languages1);
		office1.setPhoneNumber("970-527-6200");
		office1.setOfficeHours(office1Hours);
		office1.setAddress(address1);
		office2.setLanguages(languages2);
		office2.setPhoneNumber("970-527-6200");
		office2.setOfficeHours(office2Hours);
		office2.setAddress(address2);		
		
		// Set offices
		List<Office> offices = new ArrayList<Office>();
		offices.add(office1);
		offices.add(office2);
		
		expectedAgent.setOffices(offices);
		
		return expectedAgent;
	}
	
	private Agent createExpectedAgent2() {
		// This agent offers some products and has one office
		// New up the agent
		Agent expectedAgent2 = new Agent();
		
		// Set name
		expectedAgent2.setName("Debbie Peck");
		
		// Set products
		Set<Product> products = new HashSet<Product>();
		// This particular agent has partial products.
		products.add(Product.HOME_AND_PROPERTY);
		products.add(Product.AUTO);
		products.add(Product.HEALTH);
		products.add(Product.BANK);
		products.add(Product.LIFE);
		products.add(Product.ANNUITIES);
		
		expectedAgent2.setProducts(products);
		
		// New up office (and address for office) and populate
		Office office1 = new Office();
		Set<String> languages1 = new HashSet<String>();
		List<String> office1Hours = new ArrayList<String>();
		Address address1 = new Address();
		
		languages1.add("English");
		languages1.add("Spanish");
		office1Hours.add("Mon - Fri 9:00am to 5:00pm");
		office1Hours.add("Sat. by appointment only");
		address1.setLine1("9827 W. Tropicana");
		address1.setLine2("Ste 160");
		address1.setCity("Las Vegas");
		address1.setState(USState.NV);
		address1.setPostalCode("89147");
		
		office1.setLanguages(languages1);
		office1.setPhoneNumber("702-255-7666");
		office1.setOfficeHours(office1Hours);
		office1.setAddress(address1);
		
		// Set offices
		List<Office> offices = new ArrayList<Office>();
		offices.add(office1);
		
		expectedAgent2.setOffices(offices);
		
		return expectedAgent2;
	}
}
