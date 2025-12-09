package com.tejobar.tejo_bar.controller;

import com.tejobar.tejo_bar.model.Apartado;
import com.tejobar.tejo_bar.model.Apartado.EstadoApartado;
import com.tejobar.tejo_bar.model.Compra;
import com.tejobar.tejo_bar.model.Jugador;
import com.tejobar.tejo_bar.model.Persona;
import com.tejobar.tejo_bar.model.Producto;
import com.tejobar.tejo_bar.repository.ApartadoRepository;
import com.tejobar.tejo_bar.repository.CompraRepository;
import com.tejobar.tejo_bar.repository.JugadorRepository;
import com.tejobar.tejo_bar.repository.ProductoRepository;
import com.tejobar.tejo_bar.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private ApartadoRepository apartadoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @GetMapping
    public String verCarrito(Model model, Authentication authentication) {
        String email = authentication.getName();
        Persona persona = personaService.findByCorreo(email);

        List<Apartado> items = apartadoRepository.findByPersona_IdPersonaAndEstado(persona.getIdPersona(),
                EstadoApartado.pendiente);

        double total = items.stream()
                .mapToDouble(item -> item.getProducto().getPrecio() * item.getCantidad())
                .sum();

        model.addAttribute("items", items);
        model.addAttribute("total", total);

        return "carrito/index";
    }

    @PostMapping("/agregar/{idProducto}")
    public String agregarAlCarrito(@PathVariable Integer idProducto, @RequestParam(defaultValue = "1") Integer cantidad,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            // Validar cantidad
            if (cantidad == null || cantidad <= 0) {
                redirectAttributes.addFlashAttribute("error", "La cantidad debe ser mayor a 0");
                return "redirect:/productos";
            }

            String email = authentication.getName();
            Persona persona = personaService.findByCorreo(email);
            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Validar stock disponible
            if (producto.getStock() == null || producto.getStock() <= 0) {
                redirectAttributes.addFlashAttribute("error", "El producto no tiene stock disponible");
                return "redirect:/productos";
            }

            // Check if already in cart
            List<Apartado> existingItems = apartadoRepository.findByPersona_IdPersonaAndEstado(persona.getIdPersona(),
                    EstadoApartado.pendiente);
            Apartado existingItem = existingItems.stream()
                    .filter(item -> item.getProducto().getIdProducto().equals(idProducto))
                    .findFirst()
                    .orElse(null);

            int cantidadTotal = cantidad;
            if (existingItem != null) {
                cantidadTotal = existingItem.getCantidad() + cantidad;
            }

            // Validar que la cantidad total no exceda el stock
            if (cantidadTotal > producto.getStock()) {
                redirectAttributes.addFlashAttribute("error", 
                    "Stock insuficiente. Disponible: " + producto.getStock() + 
                    (existingItem != null ? ", ya tienes " + existingItem.getCantidad() + " en el carrito" : ""));
                return "redirect:/productos";
            }

            if (existingItem != null) {
                existingItem.setCantidad(cantidadTotal);
                apartadoRepository.save(existingItem);
            } else {
                Apartado newItem = new Apartado();
                newItem.setPersona(persona);
                newItem.setProducto(producto);
                newItem.setCantidad(cantidad);
                newItem.setEstado(EstadoApartado.pendiente);
                apartadoRepository.save(newItem);
            }

            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado al carrito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar al carrito: " + e.getMessage());
        }
        return "redirect:/productos";
    }

    @PostMapping("/eliminar/{idApartado}")
    public String eliminarDelCarrito(@PathVariable Integer idApartado, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Persona persona = personaService.findByCorreo(email);
            
            // Validar que el apartado pertenezca al usuario actual
            Apartado apartado = apartadoRepository.findById(idApartado)
                    .orElseThrow(() -> new RuntimeException("Apartado no encontrado"));
            
            if (!apartado.getPersona().getIdPersona().equals(persona.getIdPersona())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permisos para eliminar este apartado");
                return "redirect:/carrito";
            }
            
            apartadoRepository.deleteById(idApartado);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar del carrito: " + e.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/checkout")
    public String checkout(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Persona persona = personaService.findByCorreo(email);
            List<Apartado> items = apartadoRepository.findByPersona_IdPersonaAndEstado(persona.getIdPersona(),
                    EstadoApartado.pendiente);

            if (items.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El carrito está vacío");
                return "redirect:/carrito";
            }

            // Validar stock antes de procesar
            for (Apartado item : items) {
                Producto producto = item.getProducto();
                if (producto.getStock() == null || producto.getStock() < item.getCantidad()) {
                    redirectAttributes.addFlashAttribute("error", 
                        "Stock insuficiente para " + producto.getNombre() + 
                        ". Disponible: " + (producto.getStock() != null ? producto.getStock() : 0) + 
                        ", solicitado: " + item.getCantidad());
                    return "redirect:/carrito";
                }
            }

            // Obtener jugador para la compra
            Jugador jugador = jugadorRepository.findById(persona.getIdPersona()).orElse(null);
            if (jugador == null) {
                redirectAttributes.addFlashAttribute("error", "Debes ser un jugador para realizar compras");
                return "redirect:/carrito";
            }

            // Calcular total
            double total = items.stream()
                    .mapToDouble(item -> item.getProducto().getPrecio() * item.getCantidad())
                    .sum();

            // Crear registro de compra
            Compra compra = new Compra();
            compra.setJugador(jugador);
            compra.setTotal(total);
            compra.setFecha(LocalDate.now());
            compraRepository.save(compra);

            // Procesar cada item: cambiar estado y descontar stock
            for (Apartado item : items) {
                item.setEstado(EstadoApartado.comprado);
                apartadoRepository.save(item);
                
                // Descontar stock del producto
                Producto producto = item.getProducto();
                producto.setStock(producto.getStock() - item.getCantidad());
                productoRepository.save(producto);
            }

            redirectAttributes.addFlashAttribute("mensaje", "Compra realizada con éxito!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la compra: " + e.getMessage());
        }
        return "redirect:/productos";
    }
}
