package com.statefarm.codingcomp.directions;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.statefarm.codingcomp.configuration.CodingCompetitionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CodingCompetitionConfiguration.class)
public class FollowTheDirections {
	@Autowired
	private String stateFarmFilesPath;
	
	@Test
	public void didYouFollowTheDirections() {
		assertTrue("You didn't unzip the file.", new File(stateFarmFilesPath).exists());
	}
}
