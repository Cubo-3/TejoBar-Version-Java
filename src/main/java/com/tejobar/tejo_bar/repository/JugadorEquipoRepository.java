package com.tejobar.tejo_bar.repository;

import com.tejobar.tejo_bar.model.JugadorEquipo;
import com.tejobar.tejo_bar.model.JugadorEquipoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JugadorEquipoRepository extends JpaRepository<JugadorEquipo, JugadorEquipoId> {
    List<JugadorEquipo> findByIdJugador(Integer idJugador);
    List<JugadorEquipo> findByIdEquipo(Integer idEquipo);
    List<JugadorEquipo> findByEsCapitanTrue();
    boolean existsByIdJugadorAndIdEquipo(Integer idJugador, Integer idEquipo);
}


