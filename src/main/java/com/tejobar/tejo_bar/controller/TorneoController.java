package com.tejobar.tejo_bar.controller;

import com.tejobar.tejo_bar.model.Torneo;
import com.tejobar.tejo_bar.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/torneos")
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    @GetMapping
    public String listarTorneos(Model model) {
        List<Torneo> torneos = torneoService.findAll();
        model.addAttribute("torneos", torneos);
        return "torneos/index";
    }

    @GetMapping("/{id}")
    public String verTorneo(@PathVariable Integer id, Model model) {
        Torneo torneo = torneoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
        model.addAttribute("torneo", torneo);
        return "torneos/show";
    }
}








