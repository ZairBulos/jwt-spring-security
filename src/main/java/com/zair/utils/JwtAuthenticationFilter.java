package com.zair.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT que verifica la validez del token en cada solicitud.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final UserDetailsService userDetailsService;

    /**
     * Realiza la lógica de filtrado para autenticar las solicitudes mediante tokens JWT.
     *
     * @param request     La solicitud HTTP entrante.
     * @param response    La respuesta HTTP saliente.
     * @param filterChain La cadena de filtros para invocar después de la autenticación.
     * @throws ServletException Si ocurre un error en la servlet.
     * @throws IOException      Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (isAuthRequest(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = getTokenFromRequest(request);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = getUserDetails(username);

                if (jwtUtil.isTokenValid(token, userDetails)) {
                    setAuthentication(request, userDetails);
                }
            }

            filterChain.doFilter(request, response);
        } catch (MalformedJwtException e) {
            handleErrorToken(response, "Malformed JWT: " + e.getMessage());
        } catch (JwtException e) {
            handleErrorToken(response, "JWT Exception: " + e.getMessage());
        }
    }

    /**
     * Verifica si la solicitud es una solicitud de autenticación.
     *
     * @param request La solicitud HTTP entrante.
     * @return true si la solicitud es para autenticación, false en caso contrario.
     */
    private boolean isAuthRequest(HttpServletRequest request) {
        return request.getServletPath().contains("/api/auth");
    }

    /**
     * Obtiene el token JWT de la solicitud HTTP.
     *
     * @param request La solicitud HTTP entrante.
     * @return El token JWT si está presente en la solicitud, null si no lo está.
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            return header.replace("Bearer ", "");
        }

        return null;
    }

    /**
     * Obtiene los detalles del usuario a partir del nombre de usuario.
     *
     * @param username El nombre de usuario del usuario.
     * @return Los detalles del usuario.
     */
    private UserDetails getUserDetails(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    /**
     * Establece la autenticación en el contexto de seguridad de Spring Security.
     *
     * @param request     La solicitud HTTP entrante.
     * @param userDetails Los detalles del usuario autenticado.
     */
    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    /**
     * Maneja un error relacionado con el token JWT y responde con un mensaje de error.
     *
     * @param response La respuesta HTTP saliente.
     * @param error    El mensaje de error a incluir en la respuesta.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    private void handleErrorToken(HttpServletResponse response, String error) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + error + "\"}");
    }
}
