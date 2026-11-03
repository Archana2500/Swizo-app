package com.tap.util;

import java.nio.charset.StandardCharsets;

import org.mindrot.jbcrypt.BCrypt;

/** Password hashing with jBCrypt (jbcrypt-0.4.jar in WEB-INF/lib). */
public class PasswordUtil {

	private static final int LOG_ROUNDS = 12; // work factor: higher = slower to crack (and to log in)
	public static final int MAX_BYTES = 72; // bcrypt ignores everything after 72 bytes

	// a bcrypt hash looks like $2a$12$ + 53 characters (60 in total)
	private static final String HASH_PATTERN = "^\\$2a\\$\\d{2}\\$[./A-Za-z0-9]{53}$";

	public static String hash(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));
	}

	public static boolean isHashed(String stored) {
		return stored != null && stored.matches(HASH_PATTERN);
	}

	public static boolean verify(String password, String stored) {
		if (password == null || !isHashed(stored)) {
			return false;
		}
		try {
			return BCrypt.checkpw(password, stored);
		} catch (IllegalArgumentException e) {
			return false; // malformed hash in the database
		}
	}

	/** true if the password is short enough for bcrypt to use all of it */
	public static boolean fitsBcrypt(String password) {
		return password != null && password.getBytes(StandardCharsets.UTF_8).length <= MAX_BYTES;
	}
}