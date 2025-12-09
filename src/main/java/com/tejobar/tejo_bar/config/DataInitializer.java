package com.tejobar.tejo_bar.config;

import com.tejobar.tejo_bar.model.Persona;
import com.tejobar.tejo_bar.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin12@gmail.com";

        if (personaRepository.findByCorreo(adminEmail) == null) {
            Persona admin = new Persona();
            admin.setNombre("Admin");
            admin.setCorreo(adminEmail);
            admin.setContrasena(passwordEncoder.encode("12345"));
            admin.setNumero("0000000000");
            admin.setRol(Persona.Rol.admin);

            personaRepository.save(admin);
            System.out.println("Admin user created: " + adminEmail);
        } else {
            System.out.println("Admin user already exists: " + adminEmail);
        }
    }
}
