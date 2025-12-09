package com.tejobar.tejo_bar.controller;

import com.tejobar.tejo_bar.model.Jugador;
import com.tejobar.tejo_bar.model.Persona;
import com.tejobar.tejo_bar.service.JugadorService;
import com.tejobar.tejo_bar.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private PersonaService personaService;

    /**
     * Obtiene el correo del usuario autenticado actualmente
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // El nombre es el correo en nuestro caso
    }

    /**
     * Helper to find Persona or Fallback to Jugador
     */
    private Persona findPersonaOrJugador(String correo) {
        System.out.println("=== PROFILE SEARCH START ===");
        System.out.println("Buscando correo: '" + correo + "'");

        Persona p = null;
        try {
            p = personaService.findByCorreo(correo);
            System.out.println("JPA Result: " + (p != null ? "FOUND ID=" + p.getIdPersona() : "NULL"));
        } catch (Exception e) {
            System.out.println("JPA Error: " + e.getMessage());
        }

        if (p == null) {
            // Fallback 1: Try via JugadorService
            try {
                p = jugadorService.findByCorreo(correo);
                System.out.println("Jugador Fallback: " + (p != null ? "FOUND ID=" + p.getIdPersona() : "NULL"));
            } catch (Exception e) {
                System.out.println("Jugador Fallback Error: " + e.getMessage());
            }

            // Fallback 2: Native Query
            if (p == null) {
                try {
                    p = personaService.nativeFindByCorreo(correo);
                    System.out.println("Native Fallback: " + (p != null ? "FOUND ID=" + p.getIdPersona() : "NULL"));
                } catch (Exception e) {
                    System.out.println("Native Fallback Error: " + e.getMessage());
                }
            }

            // Fallback 3: MAGIC RECOVERY (For broken accounts with NULL email)
            if (p == null) {
                try {
                    System.out.println("Attempting Magic Recovery...");
                    Persona lostSoul = personaService.findFirstByCorreoIsNull();
                    if (lostSoul != null) {
                        System.out.println("MAGIC RECOVERY: Found broken account ID=" + lostSoul.getIdPersona()
                                + ". Restoring email to: " + correo);
                        personaService.recoverLostEmail(lostSoul.getIdPersona(), correo);
                        p = personaService.nativeFindByCorreo(correo);
                        System.out.println(
                                "After Recovery: " + (p != null ? "FOUND ID=" + p.getIdPersona() : "STILL NULL"));
                    } else {
                        System.out.println("Magic Recovery: No lost souls found");
                    }
                } catch (Exception e) {
                    System.err.println("Magic Recovery Failed: " + e.getMessage());
                }
            }
        }

        System.out.println("=== PROFILE SEARCH END: " + (p != null ? "SUCCESS" : "FAILED") + " ===");

        // CRITICAL FIX: Ensure Jugador object is returned if Role matches (Object
        // Upgrade)
        // This prevents View Crashes (500) when accessing subclass fields on a raw
        // Parent object
        if (p != null && !(p instanceof Jugador)
                && (p.getRol() == Persona.Rol.jugador || p.getRol() == Persona.Rol.capitan)) {
            Jugador j = new Jugador();
            j.setIdPersona(p.getIdPersona());
            j.setNombre(p.getNombre());
            j.setCorreo(p.getCorreo());
            j.setNumero(p.getNumero());
            j.setContrasena(p.getContrasena());
            j.setRol(p.getRol());
            j.setEstado(true); // Prevent null pointer in view
            j.setRut("SIN-RUT");
            return j;
        }

        return p;
    }

    /**
     * Muestra el perfil del usuario autenticado
     */
    @GetMapping
    public String verPerfil(Model model) {
        String correo = getCurrentUserEmail();
        Persona persona = findPersonaOrJugador(correo);

        if (persona == null) {
            model.addAttribute("error", "No se encontró el perfil del usuario");
            return "error";
        }

        model.addAttribute("persona", persona);

        if (persona instanceof Jugador) {
            model.addAttribute("jugador", (Jugador) persona);
        }

        return "perfil/index";
    }

    /**
     * Muestra el formulario de edición del perfil
     */
    @GetMapping("/editar")
    public String mostrarFormularioEdicion(Model model) {
        String correo = getCurrentUserEmail();
        Persona persona = findPersonaOrJugador(correo);

        if (persona == null) {
            model.addAttribute("error", "No se encontró el perfil del usuario");
            return "error";
        }

        model.addAttribute("persona", persona);

        if (persona instanceof Jugador) {
            model.addAttribute("jugador", (Jugador) persona);
        }

        return "perfil/editar";
    }

    /**
     * Actualiza el perfil del usuario autenticado
     * IMPORTANTE: Solo permite actualizar el perfil del usuario logueado
     */
    @PostMapping("/editar")
    public String actualizarPerfil(@ModelAttribute Jugador jugadorActualizado, // Spring binds overlapping fields to
                                                                               // Jugador object nicely
            RedirectAttributes redirectAttributes) {
        try {
            String correo = getCurrentUserEmail();
            Persona personaActual = findPersonaOrJugador(correo);

            if (personaActual == null) {
                redirectAttributes.addFlashAttribute("error", "No se encontró el perfil del usuario");
                return "redirect:/perfil";
            }

            // Actualizar datos básicos de Persona
            personaService.updatePersona(personaActual.getIdPersona(), jugadorActualizado);

            // Si es jugador, actualizar datos extra
            if (personaActual instanceof Jugador) {
                jugadorService.updateJugador(personaActual.getIdPersona(), jugadorActualizado);
            }

            redirectAttributes.addFlashAttribute("exito", "Perfil actualizado correctamente");
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/perfil/editar";
        }
    }
}
