package com.tejobar.tejo_bar.repository;

import com.tejobar.tejo_bar.model.Partido;
import com.tejobar.tejo_bar.model.Partido.EstadoPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Integer> {
    List<Partido> findByEstado(EstadoPartido estado);
    List<Partido> findByFecha(LocalDate fecha);
    List<Partido> findByCancha(Integer cancha);
}
