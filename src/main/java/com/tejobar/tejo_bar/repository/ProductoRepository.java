package com.tejobar.tejo_bar.repository;

import com.tejobar.tejo_bar.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByStockGreaterThan(Integer stock);
    List<Producto> findByFechaVencimientoLessThanEqual(LocalDate fecha);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
