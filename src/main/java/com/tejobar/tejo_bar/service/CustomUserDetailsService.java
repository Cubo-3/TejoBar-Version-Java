package com.tejobar.tejo_bar.service;

import com.tejobar.tejo_bar.model.Persona;
import com.tejobar.tejo_bar.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Persona persona = personaRepository.findByCorreo(correo);

        if (persona == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + correo);
        }

        // Convertir el rol de la persona a una autoridad de Spring Security
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + persona.getRol().name().toUpperCase());

        return User.builder()
                .username(persona.getCorreo())
                .password(persona.getContrasena())
                .authorities(Collections.singletonList(authority))
                .build();
    }
}
