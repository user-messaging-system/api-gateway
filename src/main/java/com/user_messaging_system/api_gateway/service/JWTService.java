package com.user_messaging_system.api_gateway.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.jsonwebtoken.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Service
public class JWTService {
    public static final String SECRET_KEY = "05548d6d1cbd0d0f1c43332823ed32943dac7db78257fd7529c627e7e1e6e807";

    public String generateJwtToken(String email, List<String> roles) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000))
                .signWith(getSignInKey())
                .compact();
    }

    public void validateToken(ServerHttpRequest request) {
        try {
            String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
                throw new JWTVerificationException("Invalid token");
            }

            String token = authorizationHeader.substring(7);
            validateTokenExpired(token);
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new JWTVerificationException("Geçersiz token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JWTVerificationException("Token geçersiz veya bozuk: " + e.getMessage());
        }
    }

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Collection<GrantedAuthority> extractRoles(String token) {
        return extractClaim(token, this::extractRolesFromClaims);
    }

    private List<GrantedAuthority> extractRolesFromClaims(Claims claims) {
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
    }

    public String extractToken(ServerHttpRequest request) {
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new JWTVerificationException("Authorization header is missing or invalid");
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private void validateTokenExpired(String token) {
        if (extractExpiration(token).before(new Date())) {
            throw new JWTVerificationException("Token süresi doldu");
        }
    }

    private SecretKey getSignInKey() {
        byte[] bytes = Base64.getDecoder()
                .decode(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(bytes, "HmacSHA256"); }
}