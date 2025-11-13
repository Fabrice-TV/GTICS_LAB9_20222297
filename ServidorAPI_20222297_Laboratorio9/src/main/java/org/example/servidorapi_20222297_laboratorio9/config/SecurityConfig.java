package org.example.servidorapi_20222297_laboratorio9.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para el API REST
 * Implementa autenticación básica HTTP para proteger los endpoints
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configuración de seguridad para los endpoints del API
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        // Gestión de sesiones STATELESS
        http.sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        // Deshabilitar CSRF
        http.csrf(csrf -> csrf.disable());
        
        // Habilitar autenticación básica
        http.httpBasic(basic -> {});
        
        // Configurar autorización de requests
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/product/**").authenticated()
                .anyRequest().permitAll()
        );

        return http.build();
    }

    /**
     * Configuración de usuarios
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Crear usuario con contraseña encriptada
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    /**
     * Bean para encriptar contraseñas usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
