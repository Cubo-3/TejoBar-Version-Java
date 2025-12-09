package com.tejobar.tejo_bar.controller;

import com.tejobar.tejo_bar.model.Producto;
import com.tejobar.tejo_bar.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String listarProductos(Model model) {
        List<Producto> productos = productoService.findAll();
        model.addAttribute("productos", productos);
        return "productos/index";
    }

    @GetMapping("/{id}")
    public String verProducto(@PathVariable Integer id, Model model) {
        Producto producto = productoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        model.addAttribute("producto", producto);
        return "productos/show";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/create";
    }

    @PostMapping
    public String crearProducto(@Valid @ModelAttribute Producto producto, 
                                org.springframework.validation.BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error de validación: " + 
                bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Datos inválidos"));
            return "redirect:/productos/crear";
        }
        try {
            productoService.save(producto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto creado exitosamente");
            return "redirect:/productos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos/crear";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Producto producto = productoService.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        model.addAttribute("producto", producto);
        return "productos/edit";
    }

    @PostMapping("/{id}")
    public String actualizarProducto(@PathVariable Integer id, 
                                     @Valid @ModelAttribute Producto producto,
                                     org.springframework.validation.BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error de validación: " + 
                bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Datos inválidos"));
            return "redirect:/productos/" + id + "/editar";
        }
        try {
            producto.setIdProducto(id);
            productoService.save(producto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado exitosamente");
            return "redirect:/productos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/productos/" + id + "/editar";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarProducto(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productoService.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto");
        }
        return "redirect:/productos";
    }
}


