package com.clockwise.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$
	private static final Locale locale = new Locale("pl", "PL");
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, locale);
	
	private Messages() {
	}

	public static String getString(String key) {
		try {
			return new String(RESOURCE_BUNDLE.getString(key).getBytes("ISO-8859-1"), "UTF-8");
		} catch (MissingResourceException e) {
			return "!KEY-NOT-FOUND<" + key + ">";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "!KEY-ERROR-UTF8<" + key + ">";
		}
	}
}
