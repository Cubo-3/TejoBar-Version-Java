package com.tejobar.tejo_bar.service;

import com.tejobar.tejo_bar.model.Producto;
import com.tejobar.tejo_bar.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(Integer id) {
        return productoRepository.findById(id);
    }

    public Producto save(Producto producto) {
        // Validar que el precio no sea negativo
        if (producto.getPrecio() != null && producto.getPrecio() < 0) {
            throw new RuntimeException("El precio no puede ser negativo.");
        }
        
        // Validar fecha de vencimiento
        if (producto.getFechaVencimiento() != null && producto.getFechaVencimiento().isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException("La fecha de vencimiento debe ser hoy o una fecha futura.");
        }
        
        // Validar stock
        if (producto.getStock() != null && producto.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo.");
        }
        
        // Convertir nombre a mayúsculas (como en el trigger de la BD)
        if (producto.getNombre() != null) {
            producto.setNombre(producto.getNombre().toUpperCase());
        }
        return productoRepository.save(producto);
    }

    public void deleteById(Integer id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> findDisponibles() {
        return productoRepository.findByStockGreaterThan(0);
    }

    public List<Producto> findProximosAVencer(int dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return productoRepository.findByFechaVencimientoLessThanEqual(fechaLimite);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }
}


