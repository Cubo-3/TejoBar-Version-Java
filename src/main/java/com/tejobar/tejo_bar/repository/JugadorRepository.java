package com.tejobar.tejo_bar.repository;

import com.tejobar.tejo_bar.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Integer> {
    List<Jugador> findByEstadoTrue();

    Jugador findByCorreo(String correo);

    List<Jugador> findByEstadoFalse();

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "INSERT INTO jugador (idPersona, estado, rut) SELECT p.idPersona, 1, 'SIN-RUT' FROM persona p WHERE p.rol IN ('jugador', 'capitan') AND NOT EXISTS (SELECT 1 FROM jugador j WHERE j.idPersona = p.idPersona)", nativeQuery = true)
    void syncTiposJugador();

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "UPDATE jugador SET estado = 1 WHERE estado IS NULL", nativeQuery = true)
    void fixNulls();

    // Removed fixSchemaDuplicateColumn as it is not needed and causes errors

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "INSERT INTO jugador (idPersona, estado, rut) VALUES (?1, 1, 'SIN-RUT')", nativeQuery = true)
    void insertarJugadorRaw(Integer idPersona);

    // Removed syncIdPersonaFromLegacy and syncLegacyFromIdPersona as they reference
    // invalid column id_persona
}
