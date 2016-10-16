package com.statefarm.codingcomp.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Utilities for reading files. Feel free to enhance as needed. If you enhance
 * this class, be sure to check it in and push it to GitHub.
 */
@Component
public class SFFileReader {
	@Autowired
	private String stateFarmFilesPath;

	private static final String FSLASH = "\\/";
	private static final String BSLASH = "\\\\";
	private static final String FBSLASH = "[" + FSLASH + BSLASH + "]";
	private static final Pattern AGENTS = Pattern.compile(".*" + FBSLASH + "agent" + FBSLASH + "US" + FBSLASH + ".*" + FBSLASH
			+ ".*-[A-Z0-9]{11}\\.html$");

	/**
	 * Reads a file into a String separated by newlines.
	 * 
	 * @param fileName
	 *            Full path and name of the file to read.
	 * @return
	 */
	@Cacheable("readFile")
	public String readFile(String fileName) {
		try {
			return Files.readAllLines(Paths.get(fileName)).stream().map(Object::toString).collect(Collectors.joining("\n"));
		} catch (IOException e) {
			// Making it optional for caller to handle the exception
			throw new RuntimeException("Could not read file", e);
		}
	}

	@Cacheable("htmlFiles")
	public List<String> findHtmlFiles() {
		try {
			return Files.find(Paths.get(stateFarmFilesPath), 999, (path, attr) -> StringUtils.endsWithIgnoreCase(String.valueOf(path), "html"))
					.map(String::valueOf).collect(Collectors.toList());
		} catch (IOException e) {
			// Making it optional for caller to handle the exception
			throw new RuntimeException("Error searching for files", e);
		}
	}

	// Note: Cache will not be used when invoking local method
	@Cacheable("agentFiles")
	public List<String> findAgentFiles() {
		return findHtmlFiles().stream().filter(name -> AGENTS.matcher(name).matches())
				.collect(Collectors.toList());
	}
}
