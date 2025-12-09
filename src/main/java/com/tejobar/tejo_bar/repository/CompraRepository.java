package com.tejobar.tejo_bar.repository;

import com.tejobar.tejo_bar.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {
    List<Compra> findByJugador_IdPersona(Integer idJugador);
    List<Compra> findByFecha(LocalDate fecha);
}
