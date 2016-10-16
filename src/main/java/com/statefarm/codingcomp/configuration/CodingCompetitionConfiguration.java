package com.statefarm.codingcomp.configuration;

import com.statefarm.codingcomp.utilities.SFFileReader;
import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;


@Configuration
@ComponentScan("com.statefarm.codingcomp")
@EnableCaching
public class CodingCompetitionConfiguration {
	@Autowired
	private SFFileReader sfFileReader;

	@Bean
	public String stateFarmFilesPath() {
		// Change this if you unzipped the folder elsewhere
		return Paths.get(System.getProperty("user.home"), "www.statefarm.com").toString();
	}

	// If you have memory problems, you can change cache configuration below, or remove @Cacheable annotation from SFFileReader's methods.
	// Not recommended because caching will speed up your tests dramatically
	@Bean
	public CacheManager cacheManager() {
		Cache htmlFiles = new Cache(
				new CacheConfiguration("htmlFiles", 100000)
						.eternal(true)
						.persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));

		Cache agentFiles = new Cache(
				new CacheConfiguration("agentFiles", 100000)
						.eternal(true)
						.persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));

		Cache readFile = new Cache(
				new CacheConfiguration("readFile", 250)
						.eternal(true)
						.persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));

		Cache agents = new Cache(
				new CacheConfiguration("agents", 1000)
						.eternal(true)
						.persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));

		net.sf.ehcache.CacheManager cacheManager = net.sf.ehcache.CacheManager.newInstance();
		cacheManager.addCache(htmlFiles);
		cacheManager.addCache(agentFiles);
		cacheManager.addCache(readFile);
		cacheManager.addCache(agents);

		return new EhCacheCacheManager(cacheManager);
	}
}