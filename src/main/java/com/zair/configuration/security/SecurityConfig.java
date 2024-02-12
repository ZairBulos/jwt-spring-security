package com.zair.configuration.security;

import com.zair.utils.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Clase de configuración para la seguridad de la aplicación.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter authenticationFilter;

    private final AuthenticationProvider authenticationProvider;

    /**
     * Configura el filtro de seguridad y la cadena de filtros de seguridad HTTP.
     *
     * @param http El objeto HttpSecurity utilizado para configurar la seguridad HTTP.
     * @return La cadena de filtros de seguridad configurada.
     * @throws Exception Si hay algún error durante la configuración.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilita la protección CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // Autoriza las peticiones HTTP mediante el objeto authorizationManagerRequestMatcherRegistry
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers("/api/auth/**").permitAll()    // Permite el acceso a todas las URL que comiencen con '/api/auth/'
                        .requestMatchers("/api/public/**").permitAll()  // Permite el acceso a todas las URL que comiencen con '/api/public/'
                        .anyRequest().authenticated()                     // Cualquier otra URL requiere autenticación
                )
                // Configura la gestión de sesiones como 'STATELESS' (sin estado)
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Establece el proveedor de autenticación
                .authenticationProvider(
                        authenticationProvider
                )
                // Añade el filtro de autenticación antes del filtro estándar de autenticación por nombre de usuario y contraseña
                .addFilterBefore(
                        authenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
