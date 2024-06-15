package me.macao.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl {

    private static final String SECRET = "8fad1bcb8180da2139638f2d8678627c2d6ab63b9e4f157a0420d65206c2c5a0";

    @NonNull
    public final String extractUsername(@NonNull final String token) {
        return extractClaims(token, Claims::getSubject);
    }

    @NonNull
    public final String getToken(@NonNull final UserDetails userDetails) {
        return generateToken(userDetails, new HashMap<>());
    }

    public boolean isTokenValid(
            @NonNull final String token,
            @NonNull final UserDetails userDetails
    ) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public final boolean isTokenExpired(@NonNull final String token) {
        return extractClaims(token, Claims::getExpiration)
                .before(new Date(System.currentTimeMillis()));
    }

    @NonNull
    public final String generateToken(
            @NonNull final UserDetails userDetails,
            @NonNull final Map<String, Object> claims
    ) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))   // 1 hour
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @NonNull
    public final <T> T extractClaims(
            @NonNull final String token,
            @NonNull final Function<Claims, T> claimsResolver
    ) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @NonNull
    private Claims extractAllClaims(@NonNull final String token) {
        return Jwts
                .parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @NonNull
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(SECRET)
        );
    }
}
