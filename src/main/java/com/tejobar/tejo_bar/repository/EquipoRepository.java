package com.tejobar.tejo_bar.repository;

import com.tejobar.tejo_bar.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Integer> {
    List<Equipo> findByNombreEquipoContainingIgnoreCase(String nombre);

    // La relación con jugadores y capitán es many-to-many a través de JugadorEquipo
    // No se puede hacer JOIN FETCH directo, usar consultas separadas si se necesitan los jugadores
    List<Equipo> findAll();
}
