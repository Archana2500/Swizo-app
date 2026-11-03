package com.tap.util;

public class EmailUtil {

	// TEMPORARY: prints the code in the Eclipse Console instead of emailing it.
	// Switch back to the jakarta.mail version once the mail jars are in
	// WEB-INF/lib.
	public static boolean sendResetCode(String to, String name, String code) {
		System.out.println("[DEV] Reset code for " + to + " is " + code);
		return true;
	}
}