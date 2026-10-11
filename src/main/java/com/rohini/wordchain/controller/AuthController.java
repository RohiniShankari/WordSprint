
package com.rohini.wordchain.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rohini.wordchain.dto.LoginRequest;
import com.rohini.wordchain.dto.RegisterRequest;
import com.rohini.wordchain.dto.UserResponse;
import com.rohini.wordchain.model.User;
import com.rohini.wordchain.security.JwtService;
import com.rohini.wordchain.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;
    public AuthController(AuthService authService,JwtService jwtService) {
        this.authService = authService;
        this.jwtService=jwtService;
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest request) {
        User user = authService.register(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );

        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );
    }
    

		
		@PostMapping("/login")
		public java.util.Map<String, Object> login(
		        @RequestBody LoginRequest request) {
		
		    User user = authService.login(
		        request.getUsername(),
		        request.getPassword()
		    );
		
		    String token = jwtService.generateToken(user.getUsername());
		
		    return java.util.Map.of(
		        "id", user.getId(),
		        "username", user.getUsername(),
		        "email", user.getEmail(),
		        "token", token
		    );
		}


}
