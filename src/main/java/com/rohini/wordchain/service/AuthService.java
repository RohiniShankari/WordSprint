
package com.rohini.wordchain.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rohini.wordchain.model.User;
import com.rohini.wordchain.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String email, String password) {

        username = username.trim();
        email = email.trim().toLowerCase();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new RuntimeException("All fields are required");
        }

        if (password.length() < 8) {
            throw new RuntimeException(
                    "Password must be at least 8 characters long");
        }

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

	public User login(String username, String password) {
	    User user = userRepository.findByUsername(username.trim())
	            .orElseThrow(() -> new RuntimeException("Invalid username or password"));
	
	    if (!passwordEncoder.matches(password, user.getPassword())) {
	        throw new RuntimeException("Invalid username or password");
	    }
	
	    return user;
	}

}
