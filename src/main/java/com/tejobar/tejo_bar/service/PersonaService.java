package com.tejobar.tejo_bar.service;

import com.tejobar.tejo_bar.model.Persona;
import com.tejobar.tejo_bar.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.tejobar.tejo_bar.repository.JugadorRepository jugadorRepository;

    public Persona registrar(Persona persona) {
        if (personaRepository.findByCorreo(persona.getCorreo()) != null) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        // Encriptar contraseña
        persona.setContrasena(passwordEncoder.encode(persona.getContrasena()));

        // Si es jugador o capitan, usar INSERT nativo para evitar conflicto de columnas
        if (persona.getRol() == Persona.Rol.jugador || persona.getRol() == Persona.Rol.capitan) {
            // 1. Guardar como Persona para generar ID
            Persona savedPersona = personaRepository.save(persona);

            // 2. Insertar en tabla Jugador manualmente (Double-Write Strategy)
            // 2. Insertar en tabla Jugador manualmente (Double-Write Strategy)
            try {
                jugadorRepository.insertarJugadorRaw(savedPersona.getIdPersona());
            } catch (Exception e) {
                throw new RuntimeException("Error creando jugador nativo: " + e.getMessage());
            }
            return savedPersona;
        }

        return personaRepository.save(persona);
    }

    public Persona nativeFindByCorreo(String correo) {
        return personaRepository.nativeFindByCorreo(correo);
    }

    public Persona findFirstByCorreoIsNull() {
        return personaRepository.findFirstByCorreoIsNull();
    }

    public void recoverLostEmail(Integer id, String newEmail) {
        // Use Native Update with COALESCE to update ONLY the email
        personaRepository.nativeUpdatePersona(id, null, null, newEmail);
    }

    public Persona findByCorreo(String correo) {
        return personaRepository.findByCorreo(correo);
    }

    public Persona findById(Integer id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
    }

    public Persona updatePersona(Integer id, Persona personaActualizada) {
        System.out.println("=== UPDATE PERSONA DEBUG START ===");
        System.out.println("ID: " + id);
        System.out.println("Persona Actualizada Info - Nombre: " + personaActualizada.getNombre() + ", Numero: "
                + personaActualizada.getNumero() + ", Correo: " + personaActualizada.getCorreo());

        // Obtener la persona existente de la base de datos
        Persona persona = findById(id);
        System.out.println("Persona from DB - ID: " + persona.getIdPersona() + ", Nombre: " + persona.getNombre()
                + ", Correo: " + persona.getCorreo());

        // PRESERVE original email - NEVER allow it to become null
        String correoOriginal = persona.getCorreo();
        System.out.println("correoOriginal (preserved): " + correoOriginal);

        // Actualizar solo los campos que no son null ni blank, preservando el correo
        // original
        if (personaActualizada.getNombre() != null && !personaActualizada.getNombre().isBlank()) {
            persona.setNombre(personaActualizada.getNombre());
        }

        if (personaActualizada.getNumero() != null && !personaActualizada.getNumero().isBlank()) {
            persona.setNumero(personaActualizada.getNumero());
        }

        // Asegurar que el correo nunca se actualice (siempre preservar el original)
        persona.setCorreo(correoOriginal);

        System.out.println("Final values - Nombre: " + persona.getNombre() + ", Numero: " + persona.getNumero()
                + ", Correo: " + persona.getCorreo());

        // Usar JPA estándar en lugar de consultas nativas
        Persona personaGuardada = personaRepository.save(persona);

        System.out.println("After save - Persona correo: "
                + (personaGuardada != null ? personaGuardada.getCorreo() : "NULL PERSONA!"));
        System.out.println("=== UPDATE PERSONA DEBUG END ===");

        return personaGuardada;
    }
}
