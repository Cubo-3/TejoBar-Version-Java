package com.tejobar.tejo_bar.controller;

import com.tejobar.tejo_bar.model.Cancha;
import com.tejobar.tejo_bar.service.CanchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/canchas")
public class CanchaController {

    @Autowired
    private CanchaService canchaService;

    @GetMapping
    public String listarCanchas(Model model) {
        List<Cancha> canchas = canchaService.findAll();
        model.addAttribute("canchas", canchas);
        return "canchas/index";
    }

    @GetMapping("/{id}")
    public String verCancha(@PathVariable Integer id, Model model) {
        Cancha cancha = canchaService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada"));
        model.addAttribute("cancha", cancha);
        return "canchas/show";
    }

    @PostMapping("/{id}/estado")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public String cambiarEstado(@PathVariable Integer id, @RequestParam String nuevoEstado) {
        canchaService.cambiarEstado(id, nuevoEstado);
        return "redirect:/canchas";
    }
}
