package com.tejobar.tejo_bar.repository;

import com.tejobar.tejo_bar.model.Cancha;
import com.tejobar.tejo_bar.model.Equipo;
import com.tejobar.tejo_bar.model.Partido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Integer> {
    @EntityGraph(attributePaths = { "cancha", "equipo1", "equipo2" })
    @Override
    List<Partido> findAll();

    List<Partido> findByFecha(LocalDateTime fecha);

    List<Partido> findByEquipo1_IdEquipo(Integer idEquipo);

    List<Partido> findByEquipo2_IdEquipo(Integer idEquipo);

    List<Partido> findByCancha_IdCancha(Integer idCancha);

    boolean existsByCanchaAndFecha(Cancha cancha, LocalDateTime fecha);

    boolean existsByEquipo1AndFecha(Equipo equipo, LocalDateTime fecha);

    boolean existsByEquipo2AndFecha(Equipo equipo, LocalDateTime fecha);
}
