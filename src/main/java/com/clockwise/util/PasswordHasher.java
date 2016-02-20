package com.clockwise.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

public @Component class PasswordHasher {
	private BCryptPasswordEncoder hasher = new BCryptPasswordEncoder();
	
	public String hash(String password) { return hasher.encode(password); }

	public boolean matches(String password, String hash) { return hasher.matches(password, hash); }
}
