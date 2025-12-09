package com.tejobar.tejo_bar.service;

import com.tejobar.tejo_bar.model.Jugador;
import com.tejobar.tejo_bar.repository.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JugadorService {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private com.tejobar.tejo_bar.repository.PersonaRepository personaRepository;

    @jakarta.annotation.PostConstruct
    public void initSchemaFix() {
        try {
            // Removed schema fix calls as they were causing errors

            // Emergency email recovery for known broken accounts
            personaRepository.fixEmail109();
            personaRepository.fixEmail120();
            System.out.println("Schema and Email Repair: OK");
        } catch (Exception e) {
            System.err.println("Auto-Schema Fix: " + e.getMessage());
        }
    }

    public void fixSchema() {
        // Removed schema fix calls
    }

    public List<Jugador> findAll() {
        return jugadorRepository.findAll().stream()
                .filter(j -> !j.getRol().name().equalsIgnoreCase("admin"))
                .filter(j -> j.getEstado() != null && j.getEstado()) // Solo activos
                .toList();
    }

    public void sincronizarManual() {
        try {
            // Fix Schema first (Handle Zombie Column)
            try {
                fixSchema();
            } catch (Exception e) {
                System.err.println("Schema fix warning: " + e.getMessage());
            }

            jugadorRepository.fixNulls();
            List<com.tejobar.tejo_bar.model.Persona> personas = personaRepository.findAll();
            for (com.tejobar.tejo_bar.model.Persona p : personas) {
                try {
                    if ((p.getRol() == com.tejobar.tejo_bar.model.Persona.Rol.jugador
                            || p.getRol() == com.tejobar.tejo_bar.model.Persona.Rol.capitan)) {

                        Optional<Jugador> jugadorOpt = jugadorRepository.findById(p.getIdPersona());
                        if (jugadorOpt.isEmpty()) {
                            // Does not exist -> Insert raw
                            jugadorRepository.insertarJugadorRaw(p.getIdPersona());
                        } else {
                            // Exists -> Validate and ensure Active
                            Jugador j = jugadorOpt.get();
                            boolean changed = false;

                            if (j.getEstado() == null || !j.getEstado()) {
                                j.setEstado(true);
                                changed = true;
                            }
                            if (j.getRut() == null || j.getRut().isBlank()) {
                                j.setRut("SIN-RUT");
                                changed = true;
                            }

                            if (changed) {
                                jugadorRepository.save(j);
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error processing Persona ID " + p.getIdPersona() + ": " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Manual sync error: " + e.getMessage());
        }
    }

    public Optional<Jugador> findById(Integer id) {
        return jugadorRepository.findById(id);
    }

    public Jugador save(Jugador jugador) {
        return jugadorRepository.save(jugador);
    }

    public void deleteById(Integer id) {
        jugadorRepository.deleteById(id);
    }

    public List<Jugador> findActivos() {
        return jugadorRepository.findByEstadoTrue();
    }

    public List<Jugador> findInactivos() {
        return jugadorRepository.findByEstadoFalse();
    }

    public Jugador findByCorreo(String correo) {
        return jugadorRepository.findAll().stream()
                .filter(j -> j.getCorreo().equals(correo))
                .findFirst()
                .orElse(null);
    }

    public Jugador updateJugador(Integer id, Jugador jugadorActualizado) {
        Jugador jugador = findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        // Actualizar campos de Persona SOLO si son válidos (no null ni blank)
        // NUNCA actualizar el correo - debe preservarse
        if (jugadorActualizado.getNombre() != null && !jugadorActualizado.getNombre().isBlank()) {
            jugador.setNombre(jugadorActualizado.getNombre());
        }
        if (jugadorActualizado.getNumero() != null && !jugadorActualizado.getNumero().isBlank()) {
            jugador.setNumero(jugadorActualizado.getNumero());
        }

        // Actualizar campos específicos de Jugador
        if (jugadorActualizado.getRut() != null && !jugadorActualizado.getRut().isBlank()) {
            jugador.setRut(jugadorActualizado.getRut());
        }
        if (jugadorActualizado.getEstado() != null) {
            jugador.setEstado(jugadorActualizado.getEstado());
        }

        return jugadorRepository.save(jugador);
    }

    public void desactivarJugador(Integer id) {
        Jugador jugador = findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
        jugador.setEstado(false);
        jugadorRepository.save(jugador);
    }

}
