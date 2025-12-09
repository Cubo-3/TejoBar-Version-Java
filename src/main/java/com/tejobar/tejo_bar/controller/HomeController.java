package com.tejobar.tejo_bar.controller;

import com.tejobar.tejo_bar.service.TorneoService;
import com.tejobar.tejo_bar.service.PartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private TorneoService torneoService;
    
    @Autowired
    private PartidoService partidoService;

    @GetMapping("/")
    public String home(Model model) {
        // Obtener todos los torneos vigentes (todos los torneos disponibles)
        List<com.tejobar.tejo_bar.model.Torneo> todosTorneos = torneoService.findAll();
        
        // Mostrar todos los torneos, ordenados por fecha (los más recientes primero)
        List<com.tejobar.tejo_bar.model.Torneo> torneosVigentes = todosTorneos.stream()
                .sorted((t1, t2) -> {
                    if (t1.getFecha() == null && t2.getFecha() == null) return 0;
                    if (t1.getFecha() == null) return 1;
                    if (t2.getFecha() == null) return -1;
                    return t2.getFecha().compareTo(t1.getFecha()); // Más recientes primero
                })
                .collect(Collectors.toList());
        
        model.addAttribute("torneosVigentes", torneosVigentes);
        model.addAttribute("proximosTorneos", torneosVigentes); // Mantener compatibilidad
        model.addAttribute("totalTorneos", todosTorneos.size());
        model.addAttribute("totalPartidos", partidoService.findAll().size());
        
        return "index";
    }
}
