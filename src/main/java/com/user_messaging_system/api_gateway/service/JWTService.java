package com.user_messaging_system.api_gateway.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JWTService {
    private final Key SECRET_KEY = Keys.hmacShaKeyFor("929dfc305899415c31f576fc46d8bd8b81b7e1fb6c256bbacfe0656c1da7bf11".getBytes());

    public void validateHeader(ServerHttpRequest request) {
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new JWTVerificationException("Invalid token");
        }
    }

    public void validateToken(ServerHttpRequest request) {
        try {
            String token = extractToken(request);
            validateTokenExpired(token);
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
        } catch (JWTVerificationException | IllegalArgumentException e) {
            throw new JWTVerificationException("Geçersiz token");
        }
    }

    public String extractEmail(String token) {
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(SECRET_KEY)
                .build();
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public Collection<GrantedAuthority> extractRoles(String token) {
        return extractClaim(token, claims -> {
            List<GrantedAuthority> authorities = new ArrayList<>();
            Object rolesObject = claims.get("roles");
            if (rolesObject instanceof List<?>) {
                for (Object item : (List<?>) rolesObject) {
                    if (item instanceof String) {
                        authorities.add(new SimpleGrantedAuthority((String) item));
                    }
                }
            }
            return authorities;
        });
    }

    public String extractToken(ServerHttpRequest request) {
        return Objects.requireNonNull(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).substring(7);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private void validateTokenExpired(String token) {
        try {
            if (extractExpiration(token).before(new Date())) {
                throw new JWTVerificationException("Token süresi doldu");
            }
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Geçersiz token");
        }
    }
}