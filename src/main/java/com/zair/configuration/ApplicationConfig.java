package com.zair.configuration;

import com.zair.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase de configuración de la aplicación que define los beans necesarios para la autenticación.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    /**
     * Bean que proporciona un servicio para cargar detalles de usuario basados en el nombre de usuario.
     *
     * @return Implementación de UserDetailsService que busca usuarios por su correo electrónico.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Bean que proporciona un codificador de contraseñas para encriptar y comparar contraseñas.
     *
     * @return BCryptPasswordEncoder que utiliza el algoritmo de hashing bcrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean que proporciona un proveedor de autenticación que utiliza los servicios de usuario y codificador de contraseñas.
     *
     * @return DaoAuthenticationProvider configurado con UserDetailsService y PasswordEncoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * Bean que proporciona el administrador de autenticación para la configuración de Spring Security.
     *
     * @param configuration Configuración de autenticación para obtener el administrador de autenticación.
     * @return AuthenticationManager obtenido de la configuración de autenticación.
     * @throws Exception Si hay un error al obtener el administrador de autenticación.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
