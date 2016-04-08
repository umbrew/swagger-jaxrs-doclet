package com.carma.swagger.doclet.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

/**
 * The ParameterReaderTest represents a test case for the parameter reader
 * @version $Id$
 * @author conor.roche
 */
public class ParameterReaderTest extends TestCase {

	/**
	 * This tests the add path params method can successfull
	 * extract parameters from an expression
	 */
	public void testAddPathParams() {
		ParameterReader r = new ParameterReader(null, null);

		List<String> items = new ArrayList<String>();

		List<String> expected = Arrays.asList(new String[] { "a" });
		r.addPathParams("/{a}/test", items);
		assertEquals(expected, items);

		items.clear();
		r.addPathParams("{a}", items);
		assertEquals(expected, items);

		items.clear();
		expected = Arrays.asList(new String[] { "a", "b" });
		r.addPathParams("/{a}/{b}", items);
		assertEquals(expected, items);

		items.clear();
		r.addPathParams("/{a: [0-9]+}/{b}", items);
		assertEquals(expected, items);

		items.clear();
		r.addPathParams("/{a: [0-9]+}/{b: [A-Za-z0-9]+}", items);
		assertEquals(expected, items);

	}

}
