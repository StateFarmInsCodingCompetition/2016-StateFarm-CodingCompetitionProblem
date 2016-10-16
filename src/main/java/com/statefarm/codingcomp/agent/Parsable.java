package com.statefarm.codingcomp.agent;

import org.jsoup.nodes.Element;

/**
 * An interface for anything that can be parsed from an Element
 *
 */
public interface Parsable {

	public void parse(Element element);
	
}
