package com.tejobar.tejo_bar.controller;

import com.tejobar.tejo_bar.model.Persona;
import com.tejobar.tejo_bar.service.PersonaService;
import com.tejobar.tejo_bar.service.ProductoService;
import com.tejobar.tejo_bar.service.JugadorService;
import com.tejobar.tejo_bar.service.EquipoService;
import com.tejobar.tejo_bar.service.PartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private PartidoService partidoService;

    @Autowired
    private PersonaService personaService;

    @GetMapping
    public String dashboard(Model model) {
        // Obtener el usuario autenticado
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        Persona persona = personaService.findByCorreo(correo);
        
        if (persona != null) {
            model.addAttribute("nombreUsuario", persona.getNombre());
            model.addAttribute("rolUsuario", persona.getRol().name());
        }
        
        model.addAttribute("totalProductos", productoService.findAll().size());
        model.addAttribute("totalJugadores", jugadorService.findAll().size());
        model.addAttribute("totalEquipos", equipoService.findAll().size());
        model.addAttribute("totalPartidos", partidoService.findAll().size());
        return "dashboard/index";
    }
}

