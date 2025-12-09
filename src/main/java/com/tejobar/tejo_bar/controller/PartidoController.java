package com.tejobar.tejo_bar.controller;

import com.tejobar.tejo_bar.model.Partido;
import com.tejobar.tejo_bar.service.PartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/partidos")
public class PartidoController {

    @Autowired
    private PartidoService partidoService;

    @GetMapping
    public String listarPartidos(Model model) {
        List<Partido> partidos = partidoService.findAll();
        model.addAttribute("partidos", partidos);
        return "partidos/index";
    }

    @GetMapping("/{id}")
    public String verPartido(@PathVariable Integer id, Model model) {
        Partido partido = partidoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        model.addAttribute("partido", partido);
        return "partidos/show";
    }
}








