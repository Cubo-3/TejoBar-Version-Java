package com.tejobar.tejo_bar.controller;

import com.tejobar.tejo_bar.model.Jugador;
import com.tejobar.tejo_bar.service.JugadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/jugadores")
public class JugadorController {

    @Autowired
    private JugadorService jugadorService;

    @GetMapping
    public String listarJugadores(Model model) {
        List<Jugador> jugadores = jugadorService.findAll();
        model.addAttribute("jugadores", jugadores);

        boolean isAdmin = getCurrentUserRole().equals("ROLE_ADMIN");
        model.addAttribute("isAdmin", isAdmin);

        return "jugadores/index";
    }

    @GetMapping("/{id}")
    public String verJugador(@PathVariable Integer id, Model model) {
        Jugador jugador = jugadorService.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
        model.addAttribute("jugador", jugador);
        return "jugadores/show";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Jugador jugador = jugadorService.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
        model.addAttribute("jugador", jugador);
        return "jugadores/editar";
    }

    @PostMapping("/editar/{id}")
    public String actualizarJugador(@PathVariable Integer id, @ModelAttribute Jugador jugadorActualizado) {
        jugadorService.updateJugador(id, jugadorActualizado);
        return "redirect:/jugadores";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarJugador(@PathVariable Integer id) {
        try {
            jugadorService.deleteById(id);
        } catch (Exception e) {
            jugadorService.desactivarJugador(id);
        }
        return "redirect:/jugadores";
    }

    @GetMapping("/sincronizar")
    public String sincronizarJugadores() {
        jugadorService.sincronizarManual();
        return "redirect:/jugadores";
    }

    @GetMapping("/fix-db")
    @ResponseBody
    public String fixDatabaseSchema() {
        try {
            jugadorService.fixSchema();
            return "EXITO: Base de datos reparada. La columna idPersona ahora es nulable. Intenta registrarte de nuevo.";
        } catch (Exception e) {
            return "ERROR AL REPARAR DB: " + e.getMessage();
        }
    }

    private String getCurrentUserRole() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication != null
                && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "ROLE_ADMIN";
        }
        return "ROLE_USER";
    }
}
