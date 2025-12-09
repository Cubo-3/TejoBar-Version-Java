package com.tejobar.tejo_bar.repository;

import com.tejobar.tejo_bar.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Integer> {
    List<Torneo> findByEquipo1_IdEquipo(Integer idEquipo);
    List<Torneo> findByEquipo2_IdEquipo(Integer idEquipo);
    List<Torneo> findByCancha_IdCancha(Integer idCancha);
    List<Torneo> findByFecha(LocalDateTime fecha);
}
