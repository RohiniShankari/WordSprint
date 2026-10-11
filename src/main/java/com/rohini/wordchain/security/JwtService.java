
package com.rohini.wordchain.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET =
            "VGhpcy1pcy1hLWRlbW8tc2VjcmV0LWtleS1mb3Itd29yZC1jaGFpbi1hcHA=";

    public String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(SECRET)
        );

        long currentTime = System.currentTimeMillis();

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(currentTime))
                .expiration(new Date(currentTime + 1000L * 60 * 60))
                .signWith(key)
                .compact();
    }
    public String extractUsername(String token) {
        SecretKey key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(SECRET)
        );

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}

