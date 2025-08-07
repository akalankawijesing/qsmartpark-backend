package com.smart.q.smartq.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = 86400000; // 1 day

    public String generateToken(String userId,Set<String> roles) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public Claims validateTokenAndGetClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (JwtException e) {
            return null;
        }
    }
    
    public String validateToken(String token) {
        Claims claims = validateTokenAndGetClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    public Set<String> getRolesFromToken(String token) {
        Claims claims = validateTokenAndGetClaims(token);
        if (claims != null) {
            List<String> rolesList = (List<String>) claims.get("roles");
            return new HashSet<>(rolesList); // convert to Set
        }
        return null;
    }
}
