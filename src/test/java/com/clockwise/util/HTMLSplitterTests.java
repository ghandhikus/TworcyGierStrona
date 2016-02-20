package com.clockwise.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests {@link HTMLUtils}
 * @author Daniel
 */
@WebAppConfiguration // MVC
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring-dispatcher-servlet.xml"})
public class HTMLSplitterTests {

	@Autowired HTMLUtils htmlUtils;
	
	/** Tests {@link HTMLUtils#split} */
	public @Test void splitTests() {
		String split;
		// Breaking in middle   "    |        "
		split = htmlUtils.split("txt<b>txt</b>", 5);
		assertTrue("split=("+split+")", compare(split, "txt"));
		// Breaking in middle   "   |         "
		split = htmlUtils.split("txt<b>txt</b>", 4);
		assertTrue("split=("+split+")", compare(split, "txt"));
		
		// Broken tag           "       | "
		split = htmlUtils.split("txt<b>txt", 8);
		assertTrue("split=("+split+")", compare(split, "txt<b>tx</b>"));
		
		// Breaking inside tag  "       |     "
		split = htmlUtils.split("txt<b>txt</b>", 8);
		assertTrue("split=("+split+")", compare(split, "txt<b>tx</b>"));
		
		// Correct position     "    |  "
		split = htmlUtils.split("abcdefg", 5);
		assertTrue("split=("+split+")", compare(split, "abcde"));

		// Removal of spaces    "  |   "
		split = htmlUtils.split("abcdef", 3);
		assertTrue("split=("+split+")", compare(split, "abc"));
		
		// Removal of br tags   "    |         "
		split = htmlUtils.split("a<br>fafadsffa", 5);
		assertTrue("split=("+split+")", compare(split, "a"));
		// Removal of br tags   "     |        "
		split = htmlUtils.split("a<br/>fafadsffa", 6);
		assertTrue("split=("+split+")", compare(split, "a"));
		// Removal of br tags   "      |       "
		split = htmlUtils.split("a<br />fafadsffa", 7);
		assertTrue("split=("+split+")", compare(split, "a"));
		
		// Removes br n spaces  " |                     "
		split = htmlUtils.split("asd  <br>  faf ad sf fa", 2);
		assertTrue("split=("+split+")", compare(split, "as"));
	}
	
	private boolean compare(String something, String to) {
		return something.replace(" ", "").replace("\n", "").equals(to);
	}
}
