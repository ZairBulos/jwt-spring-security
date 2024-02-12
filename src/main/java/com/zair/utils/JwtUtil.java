package com.zair.utils;

import com.zair.models.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Clase utilitaria para la generación y validación de tokens JWT.
 */
@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Value("${jwt.expiration}")
    private Long JWT_EXPIRATION;

    /**
     * Genera un token JWT para el usuario proporcionado.
     *
     * @param user El usuario para el que se genera el token.
     * @return El token JWT generado.
     */
    public String generateToken(User user) {
        // Fecha de emisión y expiración
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + JWT_EXPIRATION);

        // Claims adicionales
        Map<String, Object> claims = generateClaims(user);

        // Clave de firma
        SecretKey key = getKey();
        MacAlgorithm signatureAlgorithm = Jwts.SIG.HS256;

        return Jwts
                .builder()
                .header()
                .type("JWT")
                .and()
                .subject(user.getEmail())
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    /**
     * Verifica si el token JWT proporcionado es válido para el usuario proporcionado.
     *
     * @param token       El token JWT a validar.
     * @param userDetails Los detalles del usuario para validar el token.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        Boolean isExpired = isTokenExpired(token);

        return username.equals(userDetails.getUsername()) && !isExpired;
    }

    /**
     * Obtiene el nombre de usuario del token JWT proporcionado.
     *
     * @param token El token JWT del que se extraerá el nombre de usuario.
     * @return El nombre de usuario extraído del token.
     */
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    /**
     * Obtiene la fecha de expiración del token JWT proporcionado.
     *
     * @param token El token JWT del que se extraerá la fecha de expiración.
     * @return La fecha de expiración del token.
     */
    private Date getExpirationFromToken(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    /**
     * Obtiene la clave de firma para la generación y validación de tokens JWT.
     *
     * @return La clave de firma.
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    /**
     * Genera los claims adicionales para el token JWT a partir de los datos del usuario.
     *
     * @param user El usuario para el que se generan los claims.
     * @return Un mapa que contiene los claims adicionales.
     */
    private Map<String, Object> generateClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("role", user.getRole().name());

        return claims;
    }

    /**
     * Obtiene un claim específico del token JWT.
     *
     * @param token          El token JWT del que se extraerá el claim.
     * @param claimsResolver El resolvedor de claims que se utilizará para obtener el claim específico.
     * @param <T>            El tipo de dato del claim.
     * @return El valor del claim especificado.
     */
    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        Claims payload = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claimsResolver.apply(payload);
    }

    /**
     * Verifica si el token JWT proporcionado ha expirado.
     *
     * @param token El token JWT a verificar.
     * @return true si el token ha expirado, false en caso contrario.
     */
    private Boolean isTokenExpired(String token) {
        return getExpirationFromToken(token).before(new Date());
    }
}
