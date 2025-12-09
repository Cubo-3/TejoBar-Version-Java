package com.tejobar.tejo_bar.repository;

import com.tejobar.tejo_bar.model.Torneo;
import com.tejobar.tejo_bar.model.Torneo.EstadoTorneo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Integer> {
    @EntityGraph(attributePaths = {"cancha"})
    @Override
    List<Torneo> findAll();
    
    List<Torneo> findByEstado(EstadoTorneo estado);
    List<Torneo> findByFecha(LocalDate fecha);
    List<Torneo> findByCancha_IdCancha(Integer idCancha);
}
