package com.tejobar.tejo_bar.controller;

import com.tejobar.tejo_bar.model.Equipo;
import com.tejobar.tejo_bar.model.Jugador;
import com.tejobar.tejo_bar.service.EquipoService;
import com.tejobar.tejo_bar.service.JugadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/equipos")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;
    
    @Autowired
    private JugadorService jugadorService;

    // Admin List View
    @GetMapping
    public String listarEquipos(Model model) {
        List<Equipo> equipos = equipoService.findAll();
        
        // Crear un mapa con los datos adicionales para cada equipo
        Map<Integer, Map<String, Object>> equiposData = new HashMap<>();
        for (Equipo equipo : equipos) {
            Map<String, Object> data = new HashMap<>();
            Jugador capitan = equipoService.getCapitan(equipo);
            List<Jugador> jugadores = equipoService.getJugadores(equipo);
            
            data.put("capitan", capitan);
            data.put("jugadores", jugadores);
            equiposData.put(equipo.getIdEquipo(), data);
        }
        
        // Verificar si el usuario actual está en un equipo (solo para jugadores)
        boolean usuarioEnEquipo = false;
        Equipo equipoUsuario = null;
        try {
            String correo = SecurityContextHolder.getContext().getAuthentication().getName();
            Jugador jugador = jugadorService.findByCorreo(correo);
            if (jugador != null) {
                equipoUsuario = equipoService.getEquipoByJugador(jugador);
                usuarioEnEquipo = (equipoUsuario != null);
            }
        } catch (Exception e) {
            // Si no es jugador o hay error, simplemente no está en equipo
            usuarioEnEquipo = false;
        }
        
        model.addAttribute("equipos", equipos);
        model.addAttribute("equiposData", equiposData);
        model.addAttribute("usuarioEnEquipo", usuarioEnEquipo);
        model.addAttribute("equipoUsuario", equipoUsuario);
        return "equipos/index";
    }

    // Captain Create Team Action (Only CAPITAN)
    @PostMapping("/crear")
    @PreAuthorize("hasRole('CAPITAN')")
    public String crearEquipo(@RequestParam String nombreEquipo, RedirectAttributes redirectAttributes) {
        try {
            String correo = SecurityContextHolder.getContext().getAuthentication().getName();
            equipoService.createEquipo(nombreEquipo, correo);
            redirectAttributes.addFlashAttribute("exito", "Equipo creado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear equipo: " + e.getMessage());
        }
        return "redirect:/equipos";
    }

    // Player Join Team Action
    @PostMapping("/{id}/unirse")
    @PreAuthorize("hasRole('JUGADOR')")
    public String unirseEquipo(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            String correo = SecurityContextHolder.getContext().getAuthentication().getName();
            equipoService.joinEquipo(id, correo);
            redirectAttributes.addFlashAttribute("exito", "Te has unido al equipo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al unirse: " + e.getMessage());
        }
        return "redirect:/equipos";
    }

    // Player Leave Team Action
    @PostMapping("/salir")
    @PreAuthorize("hasRole('JUGADOR')")
    public String salirEquipo(RedirectAttributes redirectAttributes) {
        try {
            String correo = SecurityContextHolder.getContext().getAuthentication().getName();
            equipoService.leaveEquipo(correo);
            redirectAttributes.addFlashAttribute("exito", "Has abandonado el equipo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al salir: " + e.getMessage());
        }
        return "redirect:/equipos";
    }

    // Admin Remove Player Action
    @PostMapping("/{equipoId}/jugadores/{jugadorId}/eliminar")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarJugador(@PathVariable Integer equipoId, @PathVariable Integer jugadorId,
            RedirectAttributes redirectAttributes) {
        try {
            equipoService.removeJugadorFromEquipo(equipoId, jugadorId);
            redirectAttributes.addFlashAttribute("exito", "Jugador eliminado del equipo.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar jugador: " + e.getMessage());
        }
        return "redirect:/equipos";
    }

    // Admin Delete Team Action
    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarEquipo(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            equipoService.deleteEquipo(id);
            redirectAttributes.addFlashAttribute("exito", "Equipo eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar equipo: " + e.getMessage());
        }
        return "redirect:/equipos";
    }
}
