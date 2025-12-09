package com.tejobar.tejo_bar.config;

import com.tejobar.tejo_bar.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService userDetailsService;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .userDetailsService(userDetailsService)
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
                                                .ignoringRequestMatchers("/api/**")) // Solo deshabilitar para APIs si es necesario
                                .authorizeHttpRequests((requests) -> requests
                                                .requestMatchers("/", "/registro", "/login", "/css/**", "/js/**",
                                                                "/img/**")
                                                .permitAll()
                                                .requestMatchers("/productos", "/productos/{id}", "/torneos")
                                                .permitAll() // Públicos según
                                                             // Laravel
                                                .requestMatchers("/dashboard/**", "/perfil/**", "/jugadores/**",
                                                                "/equipos/**",
                                                                "/partidos/**", "/canchas/**", "/compras/**",
                                                                "/apartados/**")
                                                .authenticated()
                                                // Rutas de administración - solo ADMIN
                                                .requestMatchers("/productos/crear", "/productos/*/editar",
                                                                "/productos/*/eliminar", "/usuarios/**")
                                                .hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .formLogin((form) -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/dashboard", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout((logout) -> logout
                                                .logoutSuccessUrl("/")
                                                .permitAll());

                return http.build();
        }
}
